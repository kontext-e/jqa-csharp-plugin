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

    private final Map<String, List<InvokesModel>> processedInvocations;

    public InvocationAnalyzer(Store store, MethodCache methodCache, TypeCache typeCache) {
        this.store = store;
        this.methodCache = methodCache;
        this.typeCache = typeCache;
        processedInvocations = new HashMap<>();
    }

    protected void analyzeInvocations(MethodModel methodModel){
        addInvocations(methodModel);
        addArrayCreations(methodModel);
        addImplicitObjectCreations(methodModel);
    }

    private void addInvocations(MethodModel methodModel) {
        if (methodModel.getInvokedBy().isEmpty()) return;

        Optional<MethodDescriptor> methodDescriptor = methodCache.findAny(methodModel.getKey());
        if (!methodDescriptor.isPresent()) return;
        for (InvokesModel invokesModel : methodModel.getInvokedBy()) {
            if (hasBeenProcessed(methodModel, invokesModel)) continue;
            markInvokesModelAsProcessed(methodModel, invokesModel);

            addInvocation(methodDescriptor.get(), invokesModel); //TODO Duplicate Call to partial Constructor
        }
    }

    private boolean hasBeenProcessed(MethodModel methodModel, InvokesModel invokesModel) {
        if (!processedInvocations.containsKey(methodModel.getKey())) { return false; }

        List<InvokesModel> invokesModels = processedInvocations.get(methodModel.getKey());
        for (InvokesModel i : invokesModels){
            if (invokesModel.equals(i)){
                return true;
            }
        }
        return false;
    }

    private void markInvokesModelAsProcessed(MethodModel methodModel, InvokesModel invokesModel) {
        if (processedInvocations.containsKey(methodModel.getKey())){
            processedInvocations.get(methodModel.getKey()).add(invokesModel);
        } else {
            List<InvokesModel> newList = new ArrayList<>();
            newList.add(invokesModel);
            processedInvocations.put(methodModel.getKey(), newList );
        }
    }

    private void addInvocation(MethodDescriptor methodDescriptor, InvokesModel invokesModel) {
        MethodDescriptor invokedMethodDescriptor = methodCache.findOrCreate(invokesModel.getMethodId());
        InvokesDescriptor invokesDescriptor = store.create(invokedMethodDescriptor, InvokesDescriptor.class, methodDescriptor);
        invokesDescriptor.setLineNumber(invokesModel.getLineNumber());
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

    private void addImplicitObjectCreations(MethodModel methodModel) {
        if (methodModel.getInvokes().isEmpty()) return;

        Optional<MethodDescriptor> methodDescriptor = methodCache.findAny(methodModel.getFqn());
        if (!methodDescriptor.isPresent()) return;
        for (InvokesModel invokesModel : methodModel.getInvokes()){
            Optional<MethodDescriptor> invokedMethod = methodCache.findAny(invokesModel.getMethodId());
            if (!invokedMethod.isPresent()) continue;
            InvokesDescriptor invokesDescriptor = store.create(methodDescriptor.get(), InvokesDescriptor.class, invokedMethod.get());
            invokesDescriptor.setLineNumber(invokesModel.getLineNumber());
        }
    }
}
