package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.*;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.*;
import org.jqassistant.contrib.plugin.csharp.model.*;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonToNeo4JConverter {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(JsonToNeo4JConverter.class);

    private final Store store;
    private final File inputDirectory;

    protected final TypeCache typeCache;
    private final CSharpFileCache cSharpFileCache;
    private final EnumValueCache enumValueCache;
    public static List<FileModel> fileModelList;

    protected final MethodAnalyzer methodAnalyzer;
    protected final MemberAnalyzer memberAnalyzer;
    protected final TypeAnalyzer typeAnalyzer;

    public JsonToNeo4JConverter(Store store,
                                File inputDirectory,
                                NamespaceCache namespaceCache,
                                TypeCache typeCache,
                                CSharpFileCache cSharpFileCache,
                                MethodCache methodCache,
                                EnumValueCache enumValueCache,
                                FieldCache fieldCache,
                                PropertyCache propertyCache) {

        this.store = store;
        this.inputDirectory = inputDirectory;
        this.typeCache = typeCache;
        this.cSharpFileCache = cSharpFileCache;
        this.enumValueCache = enumValueCache;

        this.methodAnalyzer = new MethodAnalyzer(methodCache, typeCache, store);
        this.memberAnalyzer = new MemberAnalyzer(this, fieldCache, propertyCache, store);
        this.typeAnalyzer = new TypeAnalyzer(typeCache, namespaceCache, cSharpFileCache, store);

    }

    public void readAllJsonFilesAndSaveToNeo4J() {

        fileModelList = new ArrayList<>();

        readJsonFilesRecursively(inputDirectory, null);

        typeAnalyzer.createUsings();
        typeAnalyzer.createTypes();
        typeAnalyzer.linkBaseTypes();
        typeAnalyzer.linkInterfaces();
        createEnumMembers();
        createConstructors();
        methodAnalyzer.createMethods();
        methodAnalyzer.createInvocations();
        memberAnalyzer.createFields();
        memberAnalyzer.createProperties();
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

        CSharpFileDescriptor cSharpFileDescriptor = cSharpFileCache.create(fileModel.getAbsolutePath());
        cSharpFileDescriptor.setName(fileModel.getName());
        cSharpFileDescriptor.setFileName(fileModel.getRelativePath());

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

    private void createConstructors() {

        for (FileModel fileModel : fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {

                ClassDescriptor classDescriptor = typeCache.find(classModel);

                for (ConstructorModel constructorModel : classModel.getConstructors()) {
                    ConstructorDescriptor constructorDescriptor = store.create(ConstructorDescriptor.class);
                    constructorDescriptor.setName(constructorModel.getName());
                    constructorDescriptor.setVisibility(constructorModel.getAccessibility());
                    constructorDescriptor.setFirstLineNumber(constructorModel.getFirstLineNumber());
                    constructorDescriptor.setLastLineNumber(constructorModel.getLastLineNumber());
                    constructorDescriptor.setEffectiveLineCount(constructorModel.getEffectiveLineCount());

                    classDescriptor.getDeclaredMembers().add(constructorDescriptor);
                }
            }
        }
    }

    private void createEnumMembers() {

        for (FileModel fileModel : fileModelList) {
            for (EnumModel enumModel : fileModel.getEnums()) {
                EnumTypeDescriptor enumTypeDescriptor = (EnumTypeDescriptor) typeCache.get(enumModel.getKey());

                for (EnumMemberModel enumMemberModel : enumModel.getMembers()) {
                    EnumValueDescriptor enumValueDescriptor = enumValueCache.create(enumMemberModel.getKey());
                    enumValueDescriptor.setType(enumTypeDescriptor);
                }
            }
        }
    }
}
