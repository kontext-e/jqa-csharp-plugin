package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ConstructorModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberOwningTypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.StructModel;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.List;
import java.util.Optional;

public class MethodAnalyzer {

    private final ParameterAnalyzer parameterAnalyzer;

    private final MethodCache methodCache;
    private final TypeCache typeCache;

    public MethodAnalyzer(Store store, MethodCache methodCache, TypeCache typeCache) {
        this.methodCache = methodCache;
        this.typeCache = typeCache;

        parameterAnalyzer = new ParameterAnalyzer(typeCache, store);
    }

    public void createMethods(List<FileModel> fileModelList) {
        for (FileModel fileModel : fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()){
                createMethods(classModel, fileModel.getRelativePath());
            }
            for (InterfaceModel interfaceModel : fileModel.getInterfaces()){
                createMethods(interfaceModel, fileModel.getRelativePath());
            }
            for (StructModel structModel : fileModel.getStructs()){
                createMethods(structModel, fileModel.getRelativePath());
            }
        }
    }

    private void createMethods(MemberOwningTypeModel memberOwningModel, String filePath){
        Optional<TypeDescriptor> typeDescriptor = typeCache.findTypeByRelativePath(memberOwningModel.getKey(), filePath);
        if (!typeDescriptor.isPresent()) return;

        MemberOwningTypeDescriptor memberOwningTypeDescriptor = (MemberOwningTypeDescriptor) typeDescriptor.get();
        for (MethodModel methodModel : memberOwningModel.getMethods()) {
            MethodDescriptor methodDescriptor = createMethodDescriptor(methodModel);
            memberOwningTypeDescriptor.getDeclaredMembers().add(methodDescriptor);
        }
    }

    protected MethodDescriptor createMethodDescriptor(MethodModel methodModel) {
        MethodDescriptor methodDescriptor = methodCache.create(methodModel.getKey(), MethodDescriptor.class);

        fillMethodDescriptor(methodModel, methodDescriptor);
        addReturnType(methodModel, methodDescriptor);
        if (methodModel.isExtensionMethod()){
            addExtensionRelation(methodModel, methodDescriptor);
        }
        parameterAnalyzer.addParameters(methodModel, methodDescriptor);

        return methodDescriptor;
    }

    private void addExtensionRelation(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        TypeDescriptor extendedType = typeCache.findAny(methodModel.getExtendsType());
        methodDescriptor.setExtendedType(extendedType);
    }

    private static void fillMethodDescriptor(MethodModel methodModel, MethodDescriptor methodDescriptor) {

        methodDescriptor.setEffectiveLineCount(methodModel.getEffectiveLineCount());
        methodDescriptor.setLastLineNumber(methodModel.getLastLineNumber());
        methodDescriptor.setFirstLineNumber(methodModel.getFirstLineNumber());
        methodDescriptor.setName(methodModel.getName());
        methodDescriptor.setFullQualifiedName(methodModel.getFqn());
        methodDescriptor.setAccessibility(methodModel.getAccessibility());
        methodDescriptor.setCyclomaticComplexity(methodModel.getCyclomaticComplexity());
        methodDescriptor.setIsImplementation(methodModel.isImplementation());
    }

    private void addReturnType(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        TypeDescriptor returnTypeDescriptor = typeCache.findOrCreate(methodModel.getReturnType());
        methodDescriptor.setReturns(returnTypeDescriptor);
    }

    public void createConstructors(List<FileModel> fileModelList) {
        for (FileModel fileModel : fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {

                Optional<TypeDescriptor> descriptor = typeCache.findTypeByRelativePath(classModel.getKey(), fileModel.getRelativePath());
                if (!descriptor.isPresent()) continue;

                ClassDescriptor classDescriptor = (ClassDescriptor) descriptor.get();
                for (ConstructorModel constructorModel : classModel.getConstructors()) {
                    createConstructor(classDescriptor, constructorModel);
                }
            }
        }
    }

    private void createConstructor(ClassDescriptor classDescriptor, ConstructorModel constructorModel) {
        ConstructorDescriptor constructorDescriptor = methodCache.create(constructorModel.getFqn(), ConstructorDescriptor.class);
        fillMethodDescriptor(constructorModel, constructorDescriptor);
        classDescriptor.getDeclaredMembers().add(constructorDescriptor);
    }
}