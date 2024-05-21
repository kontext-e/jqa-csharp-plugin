package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.*;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberOwningTypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ProjectModel;
import org.jqassistant.contrib.plugin.csharp.model.CSharpClassesDirectoryDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonToNeo4JConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonToNeo4JConverter.class);

    private final Store store;
    private final File inputDirectory;
    private final DependencyAnalyzer dependencyAnalyzer;

    private TypeCache typeCache;
    private CSharpFileCache cSharpFileCache;
    private CSharpDirectoryCache cSharpDirectoryCache;
    private EnumValueCache enumValueCache;
    private NamespaceCache namespaceCache;
    private MethodCache methodCache;
    private FieldCache fieldCache;
    private PropertyCache propertyCache;

    private List<ProjectModel> projectModelList;

    private final MethodAnalyzer methodAnalyzer;
    private final FieldAnalyzer fieldAnalyzer;
    private final TypeAnalyzer typeAnalyzer;
    private final PartialityAnalyzer partialityAnalyzer;
    private final PropertyAnalyzer propertyAnalyzer;
    private final InvocationAnalyzer invocationAnalyzer;
    private final NamespaceAnalyzer namespaceAnalyzer;
    private final FileAnalyzer fileAnalyzer;
    private final ProjectAnalyzer projectAnalyzer;

    public JsonToNeo4JConverter(Store store, File inputDirectory) {
        initCaches(store);
        this.store = store;
        this.inputDirectory = inputDirectory;

        this.partialityAnalyzer = new PartialityAnalyzer(methodCache, typeCache);
        this.invocationAnalyzer = new InvocationAnalyzer(store, methodCache, typeCache);
        this.methodAnalyzer = new MethodAnalyzer(store, methodCache, typeCache, propertyCache);
        this.dependencyAnalyzer = new DependencyAnalyzer(cSharpFileCache, namespaceCache, typeCache, store);
        this.propertyAnalyzer = new PropertyAnalyzer(typeCache, propertyCache);
        this.fieldAnalyzer = new FieldAnalyzer(store, fieldCache, typeCache);
        this.typeAnalyzer = new TypeAnalyzer(namespaceCache, cSharpFileCache, enumValueCache, typeCache);
        this.namespaceAnalyzer = new NamespaceAnalyzer(namespaceCache);
        this.fileAnalyzer = new FileAnalyzer(cSharpFileCache, cSharpDirectoryCache);
        this.projectAnalyzer = new ProjectAnalyzer(store, cSharpFileCache);
    }

    private void initCaches(Store store) {
        namespaceCache = new NamespaceCache(store);
        cSharpFileCache = new CSharpFileCache(store);
        cSharpDirectoryCache = new CSharpDirectoryCache(store);
        methodCache = new MethodCache(store);
        enumValueCache = new EnumValueCache(store);
        fieldCache = new FieldCache(store);
        propertyCache = new PropertyCache(store);
        typeCache = new TypeCache(store);

    }


    public void readAllJsonFilesAndSaveToNeo4J() {

        projectModelList = new ArrayList<>();

        readJsonFilesRecursively(inputDirectory, null);

        createDataStructure();
        linkDataStructure();
    }

    private void createDataStructure() {
        projectModelList.forEach(projectModel -> projectModel.getFileModels().forEach(fileAnalyzer::analyzeFile));
        projectModelList.forEach(projectModel -> typeAnalyzer.createTypes(projectModel.getFileModels()));
        for (ProjectModel projectModel : projectModelList){
            projectAnalyzer.createProject(projectModel);
            for (FileModel fileModel : projectModel.getFileModels()) {
                dependencyAnalyzer.createUsings(fileModel);
                dependencyAnalyzer.linkInterfaces(fileModel);
                fileModel.getEnums().forEach(typeAnalyzer::createEnumMembers);
                for (MemberOwningTypeModel memberOwningTypeModel : fileModel.getMemberOwningTypes()) {
                    fieldAnalyzer.createFields(memberOwningTypeModel, fileModel.getRelativePath());
                    propertyAnalyzer.createProperties(memberOwningTypeModel);
                    methodAnalyzer.createMethods(memberOwningTypeModel, fileModel.getRelativePath());
                }
            }
        }
    }

    private void linkDataStructure() {
        for (ProjectModel projectModel : projectModelList) {
            for (FileModel fileModel : projectModel.getFileModels()) {
                fileModel.getClasses().forEach(dependencyAnalyzer::linkBaseTypes);
                fileModel.getRecordClasses().forEach(dependencyAnalyzer::linkBaseTypes);
                for (MemberOwningTypeModel memberOwningTypeModel : fileModel.getMemberOwningTypes()) {
                    memberOwningTypeModel.getMethods().forEach(invocationAnalyzer::analyzeInvocations);
                    memberOwningTypeModel.getConstructors().forEach(invocationAnalyzer::analyzeInvocations);
                }
            }
        }
        partialityAnalyzer.linkPartialClasses();
        partialityAnalyzer.linkPartialMethods();
        namespaceAnalyzer.constructMissingHigherLevelNamespaces();
        namespaceAnalyzer.addContainsRelationToNamespaces();
    }

    private void readJsonFilesRecursively(File currentDirectory, CSharpClassesDirectoryDescriptor parentDirectoryDescriptor) {

        File[] filesAndDirectories = currentDirectory.listFiles();
        if (filesAndDirectories == null) { return; }

        for (File file : filesAndDirectories) {
            if (file.isDirectory()) {
                scanDirectory(parentDirectoryDescriptor, file);
            } else {
                scanFile(parentDirectoryDescriptor, file);
            }
        }
    }

    private void scanFile(CSharpClassesDirectoryDescriptor parentDirectoryDescriptor, File file) {

        ProjectModel projectModel = parseAndCache(file);
        projectModelList.add(projectModel);
    }

    private ProjectModel parseAndCache(File jsonFile) {

        LOGGER.info("Processing JSON file: '{}'.", jsonFile.getName());

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(jsonFile, ProjectModel.class);
        } catch (IOException e) {
            LOGGER.error("Failed to parse JSON.", e);
        }
        return null;
    }

    private void scanDirectory(CSharpClassesDirectoryDescriptor parentDirectoryDescriptor, File file) {

        CSharpClassesDirectoryDescriptor cSharpClassesDirectoryDescriptor = store.create(CSharpClassesDirectoryDescriptor.class);
        cSharpClassesDirectoryDescriptor.setFileName(file.getName());

        if (parentDirectoryDescriptor != null) {
            parentDirectoryDescriptor.getContains().add(cSharpClassesDirectoryDescriptor);
        }

        readJsonFilesRecursively(file, cSharpClassesDirectoryDescriptor);
    }

}
