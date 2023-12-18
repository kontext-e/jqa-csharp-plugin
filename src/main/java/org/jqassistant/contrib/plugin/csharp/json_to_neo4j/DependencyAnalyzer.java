package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.UsingModel;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;

import java.util.List;

public class DependencyAnalyzer {
    private final CSharpFileCache fileCache;
    private final NamespaceCache namespaceCache;
    private final TypeCache typeCache;
    private final Store store;

    public DependencyAnalyzer(CSharpFileCache fileCache, NamespaceCache namespaceCache, TypeCache typeCache, Store store) {
        this.fileCache = fileCache;
        this.namespaceCache = namespaceCache;
        this.typeCache = typeCache;
        this.store = store;
    }

    protected void createUsings(List<FileModel> fileModelList) {

        for (FileModel fileModel : fileModelList) {
            CSharpFileDescriptor cSharpFileDescriptor = fileCache.get(fileModel.getAbsolutePath());

            for (UsingModel usingModel : fileModel.getUsings()) {
                NamespaceDescriptor namespaceDescriptor = namespaceCache.findOrCreate(usingModel.getKey());

                UsesNamespaceDescriptor usesNamespaceDescriptor = store.create(cSharpFileDescriptor, UsesNamespaceDescriptor.class, namespaceDescriptor);
                usesNamespaceDescriptor.setAlias(usingModel.getAlias());
            }
        }
    }

    protected void linkBaseTypes(List<FileModel> fileModelList) {

        for (FileModel fileModel : fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {
                ClassDescriptor classDescriptor = (ClassDescriptor) typeCache.findAny(classModel.getKey());

                if (StringUtils.isNotBlank(classModel.getBaseType())) {
                    TypeDescriptor typeDescriptor = typeCache.findOrCreate(classModel.getBaseType());
                    classDescriptor.setSuperClass(typeDescriptor);
                }
            }
        }
    }

    protected void linkInterfaces(List<FileModel> fileModelList) {

        for (FileModel fileModel : fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {
                ClassDescriptor classDescriptor = (ClassDescriptor) typeCache.findAny(classModel.getKey());

                if (CollectionUtils.isNotEmpty(classModel.getImplementedInterfaces())) {
                    for (String interfaceFqn : classModel.getImplementedInterfaces()) {
                        TypeDescriptor typeDescriptor = typeCache.findOrCreate(interfaceFqn);
                        classDescriptor.getInterfaces().add(typeDescriptor);
                    }
                }
            }

            for (InterfaceModel interfaceModel : fileModel.getInterfaces()) {
                InterfaceTypeDescriptor interfaceTypeDescriptor = (InterfaceTypeDescriptor) typeCache.findAny(interfaceModel.getKey());

                if (CollectionUtils.isNotEmpty(interfaceModel.getImplementedInterfaces())) {
                    for (String interfaceFqn : interfaceModel.getImplementedInterfaces()) {
                        TypeDescriptor typeDescriptor = typeCache.findOrCreate(interfaceFqn);
                        interfaceTypeDescriptor.getInterfaces().add(typeDescriptor);
                    }
                }
            }
        }
    }
}