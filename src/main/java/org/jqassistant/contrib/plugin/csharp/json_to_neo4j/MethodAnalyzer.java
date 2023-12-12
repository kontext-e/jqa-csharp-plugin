package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.*;
import org.jqassistant.contrib.plugin.csharp.model.*;

import java.util.*;

public class MethodAnalyzer {

    private final JsonToNeo4JConverter jsonToNeo4JConverter;
    private final Store store;

    private final MethodCache methodCache;
    private final TypeCache typeCache;
    private final PropertyCache propertyCache;

    public MethodAnalyzer(JsonToNeo4JConverter jsonToNeo4JConverter, Store store, MethodCache methodCache, PropertyCache propertyCache, TypeCache typeCache) {
        this.jsonToNeo4JConverter = jsonToNeo4JConverter;
        this.store = store;
        this.methodCache = methodCache;
        this.typeCache = typeCache;
        this.propertyCache = propertyCache;
    }

    public void createMethods() {
        for (FileModel fileModel : jsonToNeo4JConverter.getFileModelList()) {
            createMethodsForClasses(fileModel);
            createMethodsForInterfaces(fileModel);
        }
    }

    private void createMethodsForClasses(FileModel fileModel) {

        for (ClassModel classModel : fileModel.getClasses()) {
            Optional<TypeDescriptor> typeDescriptor = typeCache.findTypeByRelativePath(classModel.getKey(), fileModel.getRelativePath());
            if (!typeDescriptor.isPresent()) continue;

            ClassDescriptor classDescriptor = (ClassDescriptor) typeDescriptor.get();
            for (MethodModel methodModel : classModel.getMethods()) {
                    MethodDescriptor methodDescriptor = createMethodDescriptor(methodModel);
                    classDescriptor.getDeclaredMembers().add(methodDescriptor);
            }
        }
    }

    private void createMethodsForInterfaces(FileModel fileModel) {

        for (InterfaceModel interfaceModel : fileModel.getInterfaces()) {
            Optional<TypeDescriptor> typeDescriptor = typeCache.findTypeByRelativePath(interfaceModel.getKey(), fileModel.getRelativePath());
            if (!typeDescriptor.isPresent()) continue;

            InterfaceTypeDescriptor interfaceTypeDescriptor = (InterfaceTypeDescriptor) typeDescriptor.get();
            for (MethodModel methodModel : interfaceModel.getMethods()) {
                MethodDescriptor methodDescriptor = createMethodDescriptor(methodModel);
                interfaceTypeDescriptor.getDeclaredMembers().add(methodDescriptor);
            }
        }
    }

    public MethodDescriptor createMethodDescriptor(MethodModel methodModel) {

        MethodDescriptor methodDescriptor = methodCache.create(methodModel.getKey());
        fillMethodDescriptor(methodModel, methodDescriptor);
        TypeDescriptor returnTypeDescriptor = typeCache.findOrCreate(methodModel.getReturnType());
        methodDescriptor.setReturns(returnTypeDescriptor);
        List<ParameterDescriptor> parameterDescriptors = createParameterDescriptors(methodModel);
        methodDescriptor.getParameters().addAll(parameterDescriptors);

        return methodDescriptor;
    }

    private List<ParameterDescriptor> createParameterDescriptors(MethodModel methodModel) {

        List<ParameterDescriptor> parameterDescriptors = new ArrayList<>();
        int index = 1;

        for (ParameterModel parameterModel : methodModel.getParameters()) {
            ParameterDescriptor parameterDescriptor = store.create(ParameterDescriptor.class);
            fillParameterDescriptor(index, parameterModel, parameterDescriptor);
            parameterDescriptors.add(parameterDescriptor);

            index++;
        }

        return parameterDescriptors;
    }

    private void fillParameterDescriptor(int index, ParameterModel parameterModel, ParameterDescriptor parameterDescriptor) {

        parameterDescriptor.setIndex(index);
        TypeDescriptor parameterTypeDescriptor = typeCache.findOrCreate(parameterModel.getType());
        parameterDescriptor.setType(parameterTypeDescriptor);
        parameterDescriptor.setName(parameterModel.getName());
    }

    private static void fillMethodDescriptor(MethodModel methodModel, MethodDescriptor methodDescriptor) {

        methodDescriptor.setEffectiveLineCount(methodModel.getEffectiveLineCount());
        methodDescriptor.setLastLineNumber(methodModel.getLastLineNumber());
        methodDescriptor.setFirstLineNumber(methodModel.getFirstLineNumber());
        methodDescriptor.setName(methodModel.getName());
        methodDescriptor.setFullQualifiedName(methodModel.getFqn());
        methodDescriptor.setVisibility(methodModel.getAccessibility());
        methodDescriptor.setCyclomaticComplexity(methodModel.getCyclomaticComplexity());
        methodDescriptor.setIsImplementation(methodModel.isImplementation());
    }

    public void createInvocations() {

        for (FileModel fileModel : jsonToNeo4JConverter.getFileModelList()) {
            for (ClassModel classModel : fileModel.getClasses()) {
                for (MethodModel methodModel : classModel.getMethods()) {

                    MethodDescriptor methodDescriptor = methodCache.findAny(methodModel.getKey());
                    if (methodModel.getInvocations().isEmpty()) continue;

                    for (InvokesModel invokesModel : methodModel.getInvocations()) {
                        MethodDescriptor invokedMethodDescriptor = methodCache.findOrCreate(invokesModel.getMethodId());
                        InvokesDescriptor invokesDescriptor = store.create(methodDescriptor, InvokesDescriptor.class, invokedMethodDescriptor);
                        invokesDescriptor.setLineNumber(invokesModel.getLineNumber());
                    }
                }
            }
        }
    }

    public void createPropertyAccesses(){

        for (FileModel fileModel : jsonToNeo4JConverter.getFileModelList()) {
            for (ClassModel classModel : fileModel.getClasses()){
                for (MethodModel methodModel : classModel.getMethods()){

                    MethodDescriptor methodDescriptor = methodCache.findAny(methodModel.getKey());

                    for (MemberAccessModel memberAccessModel : methodModel.getMemberAccesses()){
                        Optional<PropertyDescriptor> propertyDescriptor = propertyCache.getPropertyFromSubstring(memberAccessModel.getMemberId());
                        if (!propertyDescriptor.isPresent()) continue;

                        MemberAccessDescriptor memberAccessDescriptor = store.create(methodDescriptor, MemberAccessDescriptor.class, propertyDescriptor.get());
                        memberAccessDescriptor.setLineNumber(memberAccessModel.getLineNumber());
                    }
                }
            }
        }
    }

    public void linkPartialMethods(){
        List<List<MethodDescriptor>> methodDescriptors = methodCache.findAllPartialMethods();
        for (List<MethodDescriptor> methodFragments : methodDescriptors){
            for (MethodDescriptor methodDescriptor : methodFragments){
                if (!methodDescriptor.getIsImplementation()) continue;
                List<MethodDescriptor> siblings = new LinkedList<>(methodFragments);
                siblings.remove(methodDescriptor);
                methodDescriptor.getMethodFragments().addAll(siblings);
            }
        }
    }
}