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

    private TypeCache typeCache;
    private CSharpFileCache cSharpFileCache;
    private EnumValueCache enumValueCache;
    private NamespaceCache namespaceCache;
    private MethodCache methodCache;
    private FieldCache fieldCache;
    private PropertyCache propertyCache;

    private List<FileModel> fileModelList;

    protected final MethodAnalyzer methodAnalyzer;
    protected final MemberAnalyzer memberAnalyzer;
    protected final TypeAnalyzer typeAnalyzer;

    public JsonToNeo4JConverter(Store store, File inputDirectory) {
        initCaches(store);
        this.store = store;
        this.inputDirectory = inputDirectory;

        this.methodAnalyzer = new MethodAnalyzer(this, store, methodCache, propertyCache, typeCache);
        this.memberAnalyzer = new MemberAnalyzer(this, store, fieldCache, propertyCache, typeCache);
        this.typeAnalyzer = new TypeAnalyzer(this, store, namespaceCache, cSharpFileCache, enumValueCache, typeCache);
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

        typeAnalyzer.createUsings();
        typeAnalyzer.createTypes();
        typeAnalyzer.linkBaseTypes();
        typeAnalyzer.linkInterfaces();
        typeAnalyzer.linkPartialClasses();
        typeAnalyzer.createEnumMembers();
        typeAnalyzer.createConstructors();
        memberAnalyzer.createFields();
        memberAnalyzer.createProperties();
        methodAnalyzer.createMethods();
        methodAnalyzer.linkPartialMethods();
        methodAnalyzer.createInvocations();
        methodAnalyzer.createPropertyAccesses();
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

    public List<FileModel> getFileModelList() {
        return fileModelList;
    }
}
