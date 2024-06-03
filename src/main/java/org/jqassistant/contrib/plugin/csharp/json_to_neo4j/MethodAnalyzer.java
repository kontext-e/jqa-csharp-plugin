package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ConstructorModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberOwningTypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.Optional;

public class MethodAnalyzer {

    private final ParameterAnalyzer parameterAnalyzer;

    private final MethodCache methodCache;
    private final TypeCache typeCache;
    private final PropertyCache propertyCache;

    public MethodAnalyzer(Store store, MethodCache methodCache, TypeCache typeCache, PropertyCache propertyCache) {
        this.methodCache = methodCache;
        this.typeCache = typeCache;
        this.propertyCache = propertyCache;

        parameterAnalyzer = new ParameterAnalyzer(typeCache, store);
    }

    public void createMethods(MemberOwningTypeModel memberOwningModel, String filePath){
        Optional<TypeDescriptor> typeDescriptor = typeCache.findTypeByRelativePath(memberOwningModel.getKey(), filePath);
        if (!typeDescriptor.isPresent()) return;

        MemberOwningTypeDescriptor memberOwningTypeDescriptor = (MemberOwningTypeDescriptor) typeDescriptor.get();
        for (MethodModel methodModel : memberOwningModel.getMethods()) {
            MethodDescriptor methodDescriptor = createMethodDescriptor(methodModel);
            memberOwningTypeDescriptor.getDeclaredMembers().add(methodDescriptor);
        }
        for (ConstructorModel constructorModel : memberOwningModel.getConstructors()){
            ConstructorDescriptor constructorDescriptor = (ConstructorDescriptor) createMethodDescriptor(constructorModel);
            memberOwningTypeDescriptor.getDeclaredMembers().add(constructorDescriptor);
        }
    }

    protected MethodDescriptor createMethodDescriptor(MethodModel methodModel) {
        MethodDescriptor methodDescriptor;
        if (methodModel instanceof ConstructorModel){
            methodDescriptor = methodCache.create(methodModel.getKey(), ConstructorDescriptor.class);
        } else {
            methodDescriptor = methodCache.create(methodModel.getKey(), MethodDescriptor.class);
        }

        fillMethodDescriptor(methodModel, methodDescriptor);
        addReturnType(methodModel, methodDescriptor);
        if (methodModel.isExtensionMethod()){
            addExtensionRelation(methodModel, methodDescriptor);
        }
        if (methodModel.getAssociatedProperty() != null){
            addAssociatedProperty(methodModel, methodDescriptor);
        }
        parameterAnalyzer.addParameters(methodModel, methodDescriptor);

        return methodDescriptor;
    }

    private void addExtensionRelation(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        TypeDescriptor extendedType = typeCache.findOrCreate(methodModel.getExtendsType());
        methodDescriptor.setExtendedType(extendedType);
        methodDescriptor.setExtensionMethod(true);
    }

    private void addAssociatedProperty(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        Optional<PropertyDescriptor> associatedProperty = propertyCache.getPropertyFromSubstring(methodModel.getAssociatedProperty());
        associatedProperty.ifPresent(methodDescriptor::setAssociatedProperty);
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
        methodDescriptor.setExtensionMethod(methodModel.isExtensionMethod());
        methodDescriptor.setStatic(methodModel.isStaticKeyword());
    }

    private void addReturnType(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        for (String type : methodModel.getReturnTypes()) {
            TypeDescriptor returnTypeDescriptor = typeCache.findOrCreate(type);
            methodDescriptor.setReturns(returnTypeDescriptor);
        }
    }
}