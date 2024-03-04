package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ArrayCreationModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InvokesModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.model.ArrayCreationDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

public class InvocationAnalyzer {

    private final Store store;
    private final MethodCache methodCache;
    private final TypeCache typeCache;

    public InvocationAnalyzer(Store store, MethodCache methodCache, TypeCache typeCache) {
        this.store = store;
        this.methodCache = methodCache;
        this.typeCache = typeCache;
    }

    protected void analyzeInvocations(MethodModel methodModel){
        addInvocations(methodModel);
        addArrayCreations(methodModel);
        addImplicitObjectCreations(methodModel);
    }

    private void addInvocations(MethodModel methodModel) {
        if (methodModel.getInvokedBy().isEmpty()) return;

        MethodDescriptor methodDescriptor = methodCache.findAny(methodModel.getKey());
        for (InvokesModel invokesModel : methodModel.getInvokedBy()) {
            addInvocation(methodDescriptor, invokesModel);
        }
    }

    private void addInvocation(MethodDescriptor methodDescriptor, InvokesModel invokesModel) {
        MethodDescriptor invokedMethodDescriptor = methodCache.findOrCreate(invokesModel.getMethodId());
        InvokesDescriptor invokesDescriptor = store.create(invokedMethodDescriptor, InvokesDescriptor.class, methodDescriptor);
        invokesDescriptor.setLineNumber(invokesModel.getLineNumber());
    }

    private void addArrayCreations(MethodModel methodModel) {
        if (methodModel.getCreatesArrays().isEmpty()) return;

        MethodDescriptor methodDescriptor = methodCache.findAny(methodModel.getFqn());
        for (ArrayCreationModel arrayCreationModel : methodModel.getCreatesArrays()){
            TypeDescriptor typeDescriptor = typeCache.findOrCreate(arrayCreationModel.getType());
            ArrayCreationDescriptor arrayCreationDescriptor = store.create(methodDescriptor, ArrayCreationDescriptor.class, typeDescriptor);
            arrayCreationDescriptor.setLineNumber(arrayCreationModel.getLineNumber());
        }
    }

    private void addImplicitObjectCreations(MethodModel methodModel) {
        if (methodModel.getInvokes().isEmpty()) return;

        MethodDescriptor methodDescriptor = methodCache.findAny(methodModel.getFqn());
        for (InvokesModel invokesModel : methodModel.getInvokes()){
            MethodDescriptor invokedMethod = methodCache.findAny(invokesModel.getMethodId());
            InvokesDescriptor invokesDescriptor = store.create(methodDescriptor, InvokesDescriptor.class, invokedMethod);
            invokesDescriptor.setLineNumber(invokesModel.getLineNumber());
        }
    }
}
