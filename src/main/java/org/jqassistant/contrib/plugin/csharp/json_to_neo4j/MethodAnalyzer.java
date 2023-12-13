package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InvocationAnalyzer;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.Optional;

public class MethodAnalyzer {

    private final JsonToNeo4JConverter jsonToNeo4JConverter;

    private final ParameterAnalyzer parameterAnalyzer;
    private final InvocationAnalyzer invocationAnalyzer;
    private final PartialityAnalyzer partialityAnalyzer;

    private final MethodCache methodCache;
    private final TypeCache typeCache;

    public MethodAnalyzer(JsonToNeo4JConverter jsonToNeo4JConverter, Store store, MethodCache methodCache, PropertyCache propertyCache, TypeCache typeCache, PartialityAnalyzer partialityAnalyzer) {
        this.jsonToNeo4JConverter = jsonToNeo4JConverter;
        this.methodCache = methodCache;
        this.typeCache = typeCache;
        this.partialityAnalyzer = partialityAnalyzer;

        parameterAnalyzer = new ParameterAnalyzer(typeCache, store);
        invocationAnalyzer = new InvocationAnalyzer(store, methodCache, propertyCache, jsonToNeo4JConverter);
    }

    public void analyze() {
        createMethods();
        partialityAnalyzer.linkPartialMethods();
        invocationAnalyzer.analyzeInvocations();
    }

    private void createMethods() {
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
        parameterAnalyzer.addParameters(methodModel, methodDescriptor);

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
}