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

import java.util.*;

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
    }

    private void addInvocations(MethodModel methodModel) {
        if (methodModel.getInvokes().isEmpty()) return;

        MethodDescriptor callerDescriptor = methodCache.findOrCreate(methodModel.getKey());

        for (InvokesModel invokesModel : methodModel.getInvokes()) {
            addInvocation(callerDescriptor, invokesModel);
        }
    }

    private void addInvocation(MethodDescriptor callerDescriptor, InvokesModel invokesModel) {
        MethodDescriptor calleeDescriptor = methodCache.findOrCreate(invokesModel.getMethodId());
        InvokesDescriptor invokesDescriptor = store.create(InvokesDescriptor.class);

        callerDescriptor.getInvokes().add(invokesDescriptor);
        invokesDescriptor.setInvokedMethod(calleeDescriptor);

        invokesDescriptor.setLineNumber(invokesModel.getLineNumber());
        for (String typeArgument : invokesModel.getTypeArguments()) {
            TypeDescriptor typeDescriptor = typeCache.findOrCreate(typeArgument);
            invokesDescriptor.getGenericTypeArguments().add(typeDescriptor);
        }
    }

    private void addArrayCreations(MethodModel methodModel) {
        if (methodModel.getCreatesArrays().isEmpty()) return;

        Optional<MethodDescriptor> methodDescriptor = methodCache.findAny(methodModel.getFqn());
        if (!methodDescriptor.isPresent()) return;
        for (ArrayCreationModel arrayCreationModel : methodModel.getCreatesArrays()){
            TypeDescriptor typeDescriptor = typeCache.findOrCreate(arrayCreationModel.getType());
            ArrayCreationDescriptor arrayCreationDescriptor = store.create(methodDescriptor.get(), ArrayCreationDescriptor.class, typeDescriptor);
            arrayCreationDescriptor.setLineNumber(arrayCreationModel.getLineNumber());
        }
    }
}
