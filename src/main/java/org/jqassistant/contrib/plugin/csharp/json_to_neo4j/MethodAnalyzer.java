package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ConstructorModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberOwningTypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
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
            for (MemberOwningTypeModel memberOwningTypeModel : fileModel.getMemberOwningTypes()){
                createMethods(memberOwningTypeModel, fileModel.getRelativePath());
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
        methodDescriptor.setExtensionMethod(true);
    }

    private static void fillMethodDescriptor(MethodModel methodModel, MethodDescriptor methodDescriptor) {

        methodDescriptor.setEffectiveLineCount(methodModel.getEffectiveLineCount());
        methodDescriptor.setLastLineNumber(methodModel.getLastLineNumber());
        methodDescriptor.setFirstLineNumber(methodModel.getFirstLineNumber());
        methodDescriptor.setName(methodModel.getName());
        methodDescriptor.setFullQualifiedName(methodModel.getFqn());
        methodDescriptor.setAccessibility(methodModel.getAccessibility());
        methodDescriptor.setCyclomaticComplexity(methodModel.getCyclomaticComplexity());
        methodDescriptor.setImplementation(methodModel.isImplementation());
        methodDescriptor.setPartial(methodModel.isPartial());
        methodDescriptor.setExtensionMethod(false);
    }

    private void addReturnType(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        TypeDescriptor returnTypeDescriptor = typeCache.findOrCreate(methodModel.getReturnType());
        methodDescriptor.setReturns(returnTypeDescriptor);
    }

    public void createConstructors(List<FileModel> fileModelList) {
        for (FileModel fileModel : fileModelList) {
            for (MemberOwningTypeModel memberOwningTypeModel : fileModel.getMemberOwningTypes()) {

                Optional<TypeDescriptor> descriptor = typeCache.findTypeByRelativePath(memberOwningTypeModel.getKey(), fileModel.getRelativePath());
                if (!descriptor.isPresent()) continue;

                MemberOwningTypeDescriptor memberOwningTypeDescriptor = (MemberOwningTypeDescriptor) descriptor.get();
                for (ConstructorModel constructorModel : memberOwningTypeModel.getConstructors()) {
                    createConstructor(memberOwningTypeDescriptor, constructorModel);
                }
            }
        }
    }

    private void createConstructor(MemberOwningTypeDescriptor memberOwningTypeDescriptor, ConstructorModel constructorModel) {
        ConstructorDescriptor constructorDescriptor = methodCache.create(constructorModel.getFqn(), ConstructorDescriptor.class);
        fillMethodDescriptor(constructorModel, constructorDescriptor);
        addReturnType(constructorModel, constructorDescriptor);
        memberOwningTypeDescriptor.getDeclaredMembers().add(constructorDescriptor);
    }
}