package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.JsonToNeo4JConverter;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberAccessDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;

import java.util.Optional;

public class InvocationAnalyzer {


    private final Store store;
    private final MethodCache methodCache;
    private final PropertyCache propertyCache;
    private final JsonToNeo4JConverter jsonToNeo4JConverter;

    public InvocationAnalyzer(Store store, MethodCache methodCache, PropertyCache propertyCache, JsonToNeo4JConverter jsonToNeo4JConverter) {
        this.store = store;
        this.methodCache = methodCache;
        this.propertyCache = propertyCache;
        this.jsonToNeo4JConverter = jsonToNeo4JConverter;
    }

    public void analyzeInvocations() {
        for (FileModel fileModel : jsonToNeo4JConverter.getFileModelList()) {
            for (ClassModel classModel : fileModel.getClasses()) {
                for (MethodModel methodModel : classModel.getMethods()) {
                    addInvocations(methodModel);
                    addPropertyAccesses(methodModel);
                }
            }
        }
    }

    private void addInvocations(MethodModel methodModel) {
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

    private void addPropertyAccesses(MethodModel methodModel){
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
