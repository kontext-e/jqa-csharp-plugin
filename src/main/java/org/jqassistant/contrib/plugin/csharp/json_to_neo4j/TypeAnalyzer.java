package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.*;
import org.jqassistant.contrib.plugin.csharp.model.*;

import java.util.Optional;

public class TypeAnalyzer {

    protected final TypeCache typeCache;
    private final Store store;
    private final NamespaceCache namespaceCache;
    private final CSharpFileCache fileCache;

    public TypeAnalyzer(TypeCache typeCache, NamespaceCache namespaceCache, CSharpFileCache fileCache, Store store) {
        this.store = store;
        this.typeCache = typeCache;
        this.fileCache = fileCache;
        this.namespaceCache = namespaceCache;
    }

    protected void createUsings() {

        for (FileModel fileModel : JsonToNeo4JConverter.fileModelList) {
            CSharpFileDescriptor cSharpFileDescriptor = fileCache.get(fileModel.getAbsolutePath());

            for (UsingModel usingModel : fileModel.getUsings()) {
                NamespaceDescriptor namespaceDescriptor = namespaceCache.findOrCreate(usingModel.getKey());

                UsesNamespaceDescriptor usesNamespaceDescriptor = store.create(cSharpFileDescriptor, UsesNamespaceDescriptor.class, namespaceDescriptor);
                usesNamespaceDescriptor.setAlias(usingModel.getAlias());
            }
        }
    }

    protected void createTypes() {

        for (FileModel fileModel : JsonToNeo4JConverter.fileModelList) {
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

        for (FileModel fileModel : JsonToNeo4JConverter.fileModelList) {
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

        for (FileModel fileModel : JsonToNeo4JConverter.fileModelList) {
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
}