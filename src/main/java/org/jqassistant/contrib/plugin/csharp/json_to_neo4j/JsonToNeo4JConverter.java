package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.EnumValueCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.FieldCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberOwningTypeModel;
import org.jqassistant.contrib.plugin.csharp.model.CSharpClassesDirectoryDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonToNeo4JConverter {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JsonToNeo4JConverter.class);

    private final Store store;
    private final File inputDirectory;
    private final DependencyAnalyzer dependencyAnalyzer;

    private TypeCache typeCache;
    private CSharpFileCache cSharpFileCache;
    private EnumValueCache enumValueCache;
    private NamespaceCache namespaceCache;
    private MethodCache methodCache;
    private FieldCache fieldCache;
    private PropertyCache propertyCache;

    private List<FileModel> fileModelList;

    private final MethodAnalyzer methodAnalyzer;
    private final MemberAnalyzer memberAnalyzer;
    private final TypeAnalyzer typeAnalyzer;
    private final PartialityAnalyzer partialityAnalyzer;
    private final PropertyAnalyzer propertyAnalyzer;
    private final InvocationAnalyzer invocationAnalyzer;

    public JsonToNeo4JConverter(Store store, File inputDirectory) {
        initCaches(store);
        this.store = store;
        this.inputDirectory = inputDirectory;

        this.partialityAnalyzer = new PartialityAnalyzer(methodCache, typeCache);
        this.invocationAnalyzer = new InvocationAnalyzer(store, methodCache, propertyCache);
        this.methodAnalyzer = new MethodAnalyzer(store, methodCache, typeCache);
        this.dependencyAnalyzer = new DependencyAnalyzer(cSharpFileCache, namespaceCache, typeCache, store);
        this.propertyAnalyzer = new PropertyAnalyzer(typeCache, propertyCache, methodAnalyzer);
        this.memberAnalyzer = new MemberAnalyzer(store, fieldCache, typeCache);
        this.typeAnalyzer = new TypeAnalyzer(namespaceCache, cSharpFileCache, enumValueCache, typeCache);
    }

    private void initCaches(Store store) {
            namespaceCache = new NamespaceCache(store);
            cSharpFileCache = new CSharpFileCache(store);
            methodCache = new MethodCache(store);
            enumValueCache = new EnumValueCache(store);
            fieldCache = new FieldCache(store);
            propertyCache = new PropertyCache(store);
            typeCache = new TypeCache(store);
    }


    public void readAllJsonFilesAndSaveToNeo4J() {

        fileModelList = new ArrayList<>();

        readJsonFilesRecursively(inputDirectory, null);

        typeAnalyzer.createTypes(fileModelList);
        for (FileModel fileModel : fileModelList) {
            dependencyAnalyzer.createUsings(fileModel);
            dependencyAnalyzer.linkInterfaces(fileModel);
            fileModel.getClasses().forEach(dependencyAnalyzer::linkBaseTypes);
            fileModel.getRecordClasses().forEach(dependencyAnalyzer::linkBaseTypes);
            fileModel.getEnums().forEach(typeAnalyzer::createEnumMembers);
            for (MemberOwningTypeModel memberOwningTypeModel : fileModel.getMemberOwningTypes()) {
                memberAnalyzer.createFields(memberOwningTypeModel, fileModel.getRelativePath());
            }
        }
        partialityAnalyzer.linkPartialClasses();
        methodAnalyzer.createConstructors(fileModelList);
        methodAnalyzer.createMethods(fileModelList);
        partialityAnalyzer.linkPartialMethods();
        invocationAnalyzer.analyzeInvocations(fileModelList);
        propertyAnalyzer.createProperties(fileModelList);
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

        FileModel fileModel = parseAndCache(file);
        fileModelList.add(fileModel);

        CSharpFileDescriptor cSharpFileDescriptor = null;
        if (fileModel != null) {
            cSharpFileDescriptor = cSharpFileCache.create(fileModel.getAbsolutePath());
            cSharpFileDescriptor.setName(fileModel.getName());
            cSharpFileDescriptor.setFileName(fileModel.getRelativePath());
        }

        if (parentDirectoryDescriptor != null) {
            parentDirectoryDescriptor.getContains().add(cSharpFileDescriptor);
        }
    }

    private FileModel parseAndCache(File jsonFile) {

        LOGGER.info("Processing JSON file: '{}'.", jsonFile);

        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return mapper.readValue(jsonFile, FileModel.class);
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
