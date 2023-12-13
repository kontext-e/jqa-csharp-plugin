package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InvokesModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberAccessModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ParameterModel;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberAccessDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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

    public void analyze() {
        createMethods();
        linkPartialMethods();
        analyzeMethodBody();
    }

    protected void createMethods() {
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

    protected MethodDescriptor createMethodDescriptor(MethodModel methodModel) {

        MethodDescriptor methodDescriptor = methodCache.create(methodModel.getKey());

        fillMethodDescriptor(methodModel, methodDescriptor);
        addReturnType(methodModel, methodDescriptor);
        addParameters(methodModel, methodDescriptor);

        return methodDescriptor;
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

    private void addReturnType(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        TypeDescriptor returnTypeDescriptor = typeCache.findOrCreate(methodModel.getReturnType());
        methodDescriptor.setReturns(returnTypeDescriptor);
    }

    private void addParameters(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        List<ParameterDescriptor> parameterDescriptors = createParameterDescriptors(methodModel);
        methodDescriptor.getParameters().addAll(parameterDescriptors);
    }

    private List<ParameterDescriptor> createParameterDescriptors(MethodModel methodModel) {

        List<ParameterDescriptor> parameterDescriptors = new ArrayList<>();
        for (int index = 0; index < methodModel.getParameters().size(); index++){
            createParameterDescriptor(methodModel, parameterDescriptors, index);
        }
        return parameterDescriptors;
    }

    private void createParameterDescriptor(MethodModel methodModel, List<ParameterDescriptor> parameterDescriptors, int index) {

        ParameterModel parameterModel = methodModel.getParameters().get(index);
        ParameterDescriptor parameterDescriptor = store.create(ParameterDescriptor.class);
        fillParameterDescriptor(index, parameterModel, parameterDescriptor);
        parameterDescriptors.add(parameterDescriptor);
    }

    private void fillParameterDescriptor(int index, ParameterModel parameterModel, ParameterDescriptor parameterDescriptor) {

        TypeDescriptor parameterTypeDescriptor = typeCache.findOrCreate(parameterModel.getType());
        parameterDescriptor.setIndex(index);
        parameterDescriptor.setType(parameterTypeDescriptor);
        parameterDescriptor.setName(parameterModel.getName());
    }

    public void linkPartialMethods(){
        List<List<MethodDescriptor>> methodDescriptors = methodCache.findAllPartialMethods();
        for (List<MethodDescriptor> methodFragments : methodDescriptors){
            for (MethodDescriptor methodDescriptor : methodFragments){
                if (!methodDescriptor.getIsImplementation()) continue;
                addSiblings(methodFragments, methodDescriptor);
            }
        }
    }

    private static void addSiblings(List<MethodDescriptor> methodFragments, MethodDescriptor methodDescriptor) {
        List<MethodDescriptor> siblings = new LinkedList<>(methodFragments);
        siblings.remove(methodDescriptor);
        methodDescriptor.getMethodFragments().addAll(siblings);
    }

    private void analyzeMethodBody() {
        for (FileModel fileModel : jsonToNeo4JConverter.getFileModelList()) {
            for (ClassModel classModel : fileModel.getClasses()) {
                for (MethodModel methodModel : classModel.getMethods()) {
                    addInvocations(methodModel);
                    addProperties(methodModel);
                }
            }
        }
    }

    protected void addInvocations(MethodModel methodModel) {
        if (methodModel.getInvocations().isEmpty()) return;

        MethodDescriptor methodDescriptor = methodCache.findAny(methodModel.getKey());
        for (InvokesModel invokesModel : methodModel.getInvocations()) {
            addInvocation(methodDescriptor, invokesModel);
        }
    }

    private void addInvocation(MethodDescriptor methodDescriptor, InvokesModel invokesModel) {
        MethodDescriptor invokedMethodDescriptor = methodCache.findOrCreate(invokesModel.getMethodId());
        InvokesDescriptor invokesDescriptor = store.create(methodDescriptor, InvokesDescriptor.class, invokedMethodDescriptor);
        invokesDescriptor.setLineNumber(invokesModel.getLineNumber());
    }

    protected void addProperties(MethodModel methodModel){
        MethodDescriptor methodDescriptor = methodCache.findAny(methodModel.getKey());

        for (MemberAccessModel memberAccessModel : methodModel.getMemberAccesses()){
            addProperty(methodDescriptor, memberAccessModel);
        }
    }

    private void addProperty(MethodDescriptor methodDescriptor, MemberAccessModel memberAccessModel) {
        Optional<PropertyDescriptor> propertyDescriptor = propertyCache.getPropertyFromSubstring(memberAccessModel.getMemberId());
        if (!propertyDescriptor.isPresent()) return;

        MemberAccessDescriptor memberAccessDescriptor = store.create(methodDescriptor, MemberAccessDescriptor.class, propertyDescriptor.get());
        memberAccessDescriptor.setLineNumber(memberAccessModel.getLineNumber());
    }
}