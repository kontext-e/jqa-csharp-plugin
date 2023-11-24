package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.EnumValueCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.*;
import org.jqassistant.contrib.plugin.csharp.model.*;

import java.util.*;

public class TypeAnalyzer {

    private final JsonToNeo4JConverter jsonToNeo4JConverter;
    private final Store store;

    private final TypeCache typeCache;
    private final NamespaceCache namespaceCache;
    private final CSharpFileCache fileCache;
    private final EnumValueCache enumValueCache;

    public TypeAnalyzer(JsonToNeo4JConverter jsonToNeo4JConverter, Store store, TypeCache typeCache, NamespaceCache namespaceCache,
                        CSharpFileCache fileCache, EnumValueCache enumValueCache) {
        this.jsonToNeo4JConverter = jsonToNeo4JConverter;
        this.store = store;
        this.typeCache = typeCache;
        this.namespaceCache = namespaceCache;
        this.fileCache = fileCache;
        this.enumValueCache = enumValueCache;
    }

    protected void createUsings() {

        for (FileModel fileModel : jsonToNeo4JConverter.fileModelList) {
            CSharpFileDescriptor cSharpFileDescriptor = fileCache.get(fileModel.getAbsolutePath());

            for (UsingModel usingModel : fileModel.getUsings()) {
                NamespaceDescriptor namespaceDescriptor = namespaceCache.findOrCreate(usingModel.getKey());

                UsesNamespaceDescriptor usesNamespaceDescriptor = store.create(cSharpFileDescriptor, UsesNamespaceDescriptor.class, namespaceDescriptor);
                usesNamespaceDescriptor.setAlias(usingModel.getAlias());
            }
        }
    }

    protected void createTypes() {

        for (FileModel fileModel : jsonToNeo4JConverter.fileModelList) {
            CSharpFileDescriptor cSharpFileDescriptor = fileCache.get(fileModel.getAbsolutePath());

            for (ClassModel classModel : fileModel.getClasses()) {
                createType(cSharpFileDescriptor, classModel);
            }

            for (EnumModel enumModel : fileModel.getEnums()) {
                createType(cSharpFileDescriptor, enumModel);
            }

            for (InterfaceModel interfaceModel : fileModel.getInterfaces()) {
                createType(cSharpFileDescriptor, interfaceModel);
            }
        }
    }

    private void createType(CSharpFileDescriptor cSharpFileDescriptor, TypeModel typeModel) {
        TypeDescriptor typeDescriptor = typeCache.create(typeModel);
        cSharpFileDescriptor.getTypes().add(typeDescriptor);

        findOrCreateNamespace(typeModel.getFqn())
                .ifPresent(namespaceDescriptor -> namespaceDescriptor.getContains().add(typeDescriptor));
    }

    private Optional<NamespaceDescriptor> findOrCreateNamespace(String fqn) {
        if (!fqn.contains(".")) {
            return Optional.empty();
        }

        String namespaceFqn = fqn.substring(0, fqn.lastIndexOf("."));
        return Optional.of(namespaceCache.findOrCreate(namespaceFqn));
    }

    protected void linkBaseTypes() {

        for (FileModel fileModel : jsonToNeo4JConverter.fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {
                ClassDescriptor classDescriptor = (ClassDescriptor) typeCache.get(classModel.getKey());

                if (StringUtils.isNotBlank(classModel.getBaseType())) {
                    TypeDescriptor typeDescriptor = typeCache.findOrCreateEmptyClass(classModel.getBaseType());
                    classDescriptor.setSuperClass(typeDescriptor);
                }
            }
        }
    }

    protected void linkInterfaces() {

        for (FileModel fileModel : jsonToNeo4JConverter.fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {
                ClassDescriptor classDescriptor = (ClassDescriptor) typeCache.get(classModel.getKey());

                if (CollectionUtils.isNotEmpty(classModel.getImplementedInterfaces())) {
                    for (String interfaceFqn : classModel.getImplementedInterfaces()) {
                        TypeDescriptor typeDescriptor = typeCache.findOrCreateEmptyInterface(interfaceFqn);
                        classDescriptor.getInterfaces().add(typeDescriptor);
                    }
                }
            }

            for (InterfaceModel interfaceModel : fileModel.getInterfaces()) {
                InterfaceTypeDescriptor interfaceTypeDescriptor = (InterfaceTypeDescriptor) typeCache.get(interfaceModel.getKey());

                if (CollectionUtils.isNotEmpty(interfaceModel.getImplementedInterfaces())) {
                    for (String interfaceFqn : interfaceModel.getImplementedInterfaces()) {
                        TypeDescriptor typeDescriptor = typeCache.findOrCreateEmptyInterface(interfaceFqn);
                        interfaceTypeDescriptor.getInterfaces().add(typeDescriptor);
                    }
                }
            }
        }
    }

    public void createEnumMembers() {

        for (FileModel fileModel : jsonToNeo4JConverter.fileModelList) {
            for (EnumModel enumModel : fileModel.getEnums()) {
                EnumTypeDescriptor enumTypeDescriptor = (EnumTypeDescriptor) typeCache.get(enumModel.getKey());

                for (EnumMemberModel enumMemberModel : enumModel.getMembers()) {
                    EnumValueDescriptor enumValueDescriptor = enumValueCache.create(enumMemberModel.getKey());
                    enumValueDescriptor.setType(enumTypeDescriptor);
                }
            }
        }
    }

    public void createConstructors() {
        for (FileModel fileModel : jsonToNeo4JConverter.fileModelList) {
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
}