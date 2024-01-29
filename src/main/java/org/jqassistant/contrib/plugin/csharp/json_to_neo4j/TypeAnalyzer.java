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
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.RecordClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.RecordStructModel;
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

            for (RecordClassModel recordClassModel : fileModel.getRecordClasses()){
                createType(cSharpFileDescriptor, recordClassModel);
            }

            for (RecordStructModel recordStructModel : fileModel.getRecordStructs()){
                createType(cSharpFileDescriptor, recordStructModel);
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
        addGeneralInformation(descriptor, typeModel);
        addTypeSpecificInformation(descriptor, typeModel);
    }

    private static void addGeneralInformation(TypeDescriptor descriptor, TypeModel typeModel) {
        descriptor.setName(typeModel.getName());
        descriptor.setFullQualifiedName(typeModel.getFqn());
        descriptor.setMd5(typeModel.getMd5());
        descriptor.setRelativePath(typeModel.getRelativePath());
        descriptor.setFirstLineNumber(typeModel.getFirstLineNumber());
        descriptor.setLastLineNumber(typeModel.getLastLineNumber());
        descriptor.setEffectiveLineCount(typeModel.getEffectiveLineCount());
    }

    private static void addTypeSpecificInformation(TypeDescriptor descriptor, TypeModel typeModel) {
        if (typeModel instanceof InterfaceModel){
            addInterfaceInformation((InterfaceTypeDescriptor) descriptor, (InterfaceModel) typeModel);
        }
        if (typeModel instanceof ClassModel) {
            addClassInformation((ClassDescriptor) descriptor, (ClassModel) typeModel);
        }
        if (typeModel instanceof StructModel){
            addStructInformation((StructDescriptor) descriptor, (StructModel) typeModel);
        }
    }

    private static void addInterfaceInformation(InterfaceTypeDescriptor descriptor, InterfaceModel typeModel) {
        descriptor.setAccessibility(typeModel.getAccessibility());
        descriptor.setPartial(typeModel.isPartial());
    }

    private static void addClassInformation(ClassDescriptor descriptor, ClassModel typeModel) {
        descriptor.setPartial(typeModel.isPartial());
        descriptor.setAbstract(typeModel.isAbstractKeyword());
        descriptor.setSealed(typeModel.isSealed());
        descriptor.setStatic(typeModel.isStaticKeyword());
        descriptor.setAccessibility(typeModel.getAccessibility());
    }

    private static void addStructInformation(StructDescriptor descriptor, StructModel typeModel) {
        descriptor.setPartial(typeModel.isPartial());
        descriptor.setAccessibility(typeModel.getAccessibility());
        descriptor.setReadOnly(typeModel.isReadOnly());
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