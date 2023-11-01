package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.*;
import org.jqassistant.contrib.plugin.csharp.model.*;

public class MethodAnalyzer {

    private final MethodCache methodCache;
    private final TypeCache typeCache;
    private final Store store;

    public MethodAnalyzer(MethodCache methodCache, TypeCache typeCache, Store store) {
        this.methodCache = methodCache;
        this.typeCache = typeCache;
        this.store = store;
    }

    public void createMethods() {
        for (FileModel fileModel : JsonToNeo4JConverter.fileModelList) {
            createMethodsForClasses(fileModel);
            createMethodsForInterfaces(fileModel);
        }
    }

    private void createMethodsForClasses(FileModel fileModel) {
        for (ClassModel classModel : fileModel.getClasses()) {
            ClassDescriptor classDescriptor = (ClassDescriptor) typeCache.get(classModel.getKey());

            for (MethodModel methodModel : classModel.getMethods()) {
                MethodDescriptor methodDescriptor = createMethodDescriptor(methodModel);
                classDescriptor.getDeclaredMembers().add(methodDescriptor);
            }
        }
    }

    private void createMethodsForInterfaces(FileModel fileModel) {

        for (InterfaceModel interfaceModel : fileModel.getInterfaces()) {
            InterfaceTypeDescriptor interfaceTypeDescriptor = (InterfaceTypeDescriptor) typeCache.get(interfaceModel.getKey());

            for (MethodModel methodModel : interfaceModel.getMethods()) {
                MethodDescriptor methodDescriptor = createMethodDescriptor(methodModel);
                interfaceTypeDescriptor.getDeclaredMembers().add(methodDescriptor);
            }
        }
    }

    public MethodDescriptor createMethodDescriptor(MethodModel methodModel) {

        MethodDescriptor methodDescriptor = methodCache.create(methodModel.getKey());
        methodDescriptor.setEffectiveLineCount(methodModel.getEffectiveLineCount());
        methodDescriptor.setLastLineNumber(methodModel.getLastLineNumber());
        methodDescriptor.setFirstLineNumber(methodModel.getFirstLineNumber());
        methodDescriptor.setName(methodModel.getName());
        methodDescriptor.setFullQualifiedName(methodModel.getFqn());
        methodDescriptor.setVisibility(methodModel.getAccessibility());
        methodDescriptor.setCyclomaticComplexity(methodModel.getCyclomaticComplexity());
        TypeDescriptor returnTypeDescriptor = typeCache.findOrCreate(methodModel.getReturnType());
        methodDescriptor.setReturns(returnTypeDescriptor);

        int index = 1;
        for (ParameterModel parameterModel : methodModel.getParameters()) {

            ParameterDescriptor parameterDescriptor = store.create(ParameterDescriptor.class);
            parameterDescriptor.setIndex(index);
            TypeDescriptor parameterTypeDescriptor = typeCache.findOrCreate(parameterModel.getType());
            parameterDescriptor.setType(parameterTypeDescriptor);
            parameterDescriptor.setName(parameterModel.getName());

            methodDescriptor.getParameters().add(parameterDescriptor);
            index++;
        }
        return methodDescriptor;
    }

    public void createInvocations() {

        for (FileModel fileModel : JsonToNeo4JConverter.fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {
                for (MethodModel methodModel : classModel.getMethods()) {
                    MethodDescriptor methodDescriptor = methodCache.find(methodModel.getKey());

                    for (InvokesModel invokesModel : methodModel.getInvocations()) {
                        MethodDescriptor invokedMethodDescriptor = methodCache.findOrCreate(invokesModel.getMethodId());
                        InvokesDescriptor invokesDescriptor = store.create(methodDescriptor, InvokesDescriptor.class, invokedMethodDescriptor);
                        invokesDescriptor.setLineNumber(invokesModel.getLineNumber());
                    }
                }
            }
        }
    }
}