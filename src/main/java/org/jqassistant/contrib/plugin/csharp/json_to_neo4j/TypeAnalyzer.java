package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.EnumValueCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.EnumMemberModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.EnumModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.StructModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.TypeModel;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.StructDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.List;

public class TypeAnalyzer {

    private final TypeCache typeCache;
    private final NamespaceCache namespaceCache;
    private final CSharpFileCache fileCache;
    private final EnumValueCache enumValueCache;

    public TypeAnalyzer(NamespaceCache namespaceCache, CSharpFileCache fileCache, EnumValueCache enumValueCache, TypeCache typeCache) {
        this.typeCache = typeCache;
        this.namespaceCache = namespaceCache;
        this.fileCache = fileCache;
        this.enumValueCache = enumValueCache;
    }

    protected void createTypes(List<FileModel> fileModelList) {

        for (FileModel fileModel : fileModelList) {
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

            for (StructModel structModel : fileModel.getStructs()){
                createType(cSharpFileDescriptor, structModel);
            }
        }
    }

    private void createType(CSharpFileDescriptor cSharpFileDescriptor, TypeModel typeModel) {

        TypeDescriptor typeDescriptor = typeCache.create(typeModel);
        fillDescriptor(typeDescriptor, typeModel);
        cSharpFileDescriptor.getTypes().add(typeDescriptor);

        if (!typeModel.getFqn().contains(".")) return;
        String namespace = typeModel.getFqn().substring(0, typeModel.getFqn().lastIndexOf("."));

        NamespaceDescriptor namespaceDescriptor = namespaceCache.findOrCreate(namespace);
        namespaceDescriptor.getContains().add(typeDescriptor);
    }


    private void fillDescriptor(TypeDescriptor descriptor, TypeModel typeModel) {
        descriptor.setName(typeModel.getName());
        descriptor.setFullQualifiedName(typeModel.getFqn());
        descriptor.setMd5(typeModel.getMd5());
        descriptor.setRelativePath(typeModel.getRelativePath());
        descriptor.setFirstLineNumber(typeModel.getFirstLineNumber());
        descriptor.setLastLineNumber(typeModel.getLastLineNumber());
        descriptor.setEffectiveLineCount(typeModel.getEffectiveLineCount());

        if (typeModel instanceof InterfaceModel){
            InterfaceModel interfaceModel = (InterfaceModel) typeModel;
            InterfaceTypeDescriptor interfaceDescriptor = (InterfaceTypeDescriptor) descriptor;
            interfaceDescriptor.setAccessibility(interfaceModel.getAccessibility());
            interfaceDescriptor.setPartial(interfaceModel.isPartial());
        }

        if (typeModel instanceof ClassModel) {
            ClassModel classModel = (ClassModel) typeModel;
            ClassDescriptor classDescriptor = (ClassDescriptor) descriptor;
            classDescriptor.setPartial(classModel.isPartial());
            classDescriptor.setAbstract(classModel.isAbstractKeyword());
            classDescriptor.setSealed(classModel.isSealed());
            classDescriptor.setStatic(classModel.isStaticKeyword());
        }

        if (typeModel instanceof StructModel){
            StructModel structModel = (StructModel) typeModel;
            StructDescriptor structDescriptor = (StructDescriptor) descriptor;
            structDescriptor.setPartial(structModel.isPartial());
        }
    }

    public void createEnumMembers(List<FileModel> fileModelList) {

        for (FileModel fileModel : fileModelList) {
            for (EnumModel enumModel : fileModel.getEnums()) {
                EnumTypeDescriptor enumTypeDescriptor = (EnumTypeDescriptor) typeCache.findAny(enumModel.getKey());

                for (EnumMemberModel enumMemberModel : enumModel.getMembers()) {
                    EnumValueDescriptor enumValueDescriptor = enumValueCache.create(enumMemberModel.getKey());
                    enumValueDescriptor.setType(enumTypeDescriptor);
                }
            }
        }
    }
}