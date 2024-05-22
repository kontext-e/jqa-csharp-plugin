package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.*;
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

            for (TypeModel typeModel : fileModel.getTypeModels()){
                createType(cSharpFileDescriptor, typeModel);
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
        descriptor.setRelativePath(typeModel.getRelativePath());
        descriptor.setFirstLineNumber(typeModel.getFirstLineNumber());
        descriptor.setLastLineNumber(typeModel.getLastLineNumber());
        descriptor.setEffectiveLineCount(typeModel.getEffectiveLineCount());
        descriptor.setAccessibility(typeModel.getAccessibility());
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

    private static void addInterfaceInformation(InterfaceTypeDescriptor descriptor, InterfaceModel interfaceModel) {
        descriptor.setPartial(interfaceModel.isPartial());
    }

    private static void addClassInformation(ClassDescriptor descriptor, ClassModel classModel) {
        descriptor.setPartial(classModel.isPartial());
        descriptor.setAbstract(classModel.isAbstractKeyword());
        descriptor.setSealed(classModel.isSealed());
        descriptor.setStatic(classModel.isStaticKeyword());
    }

    private static void addStructInformation(StructDescriptor descriptor, StructModel structModel) {
        descriptor.setPartial(structModel.isPartial());
        descriptor.setReadOnly(structModel.isReadOnly());
    }

    public void createEnumMembers(EnumModel enumModel) {
        EnumTypeDescriptor enumTypeDescriptor = (EnumTypeDescriptor) typeCache.findAny(enumModel.getKey());

        for (EnumMemberModel enumMemberModel : enumModel.getMembers()) {
            EnumValueDescriptor enumValueDescriptor = enumValueCache.create(enumMemberModel.getKey());
            enumValueDescriptor.getTypes().add(enumTypeDescriptor);
            enumValueDescriptor.setName(enumMemberModel.getName());
        }
    }
}