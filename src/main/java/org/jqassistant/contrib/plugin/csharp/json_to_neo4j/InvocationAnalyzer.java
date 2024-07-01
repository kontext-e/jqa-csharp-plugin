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
    }

    private void addInvocations(MethodModel methodModel) {
        if (methodModel.getInvokedBy().isEmpty()) return;

        Optional<MethodDescriptor> methodDescriptor = methodCache.findAny(methodModel.getKey());
        if (!methodDescriptor.isPresent()) return;
        for (InvokesModel invokesModel : methodModel.getInvokedBy()) {
            //The following two lines are an ugly workaround for the issue that for partial Methods
            //each part of the method is asked "who are you invoked by?" so invocations are processed multiple times
            if (hasBeenProcessed(methodModel, invokesModel)) continue;
            markInvokesModelAsProcessed(methodModel, invokesModel);

            addInvocation(methodDescriptor.get(), invokesModel);
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

    private void addInvocation(MethodDescriptor invokedMethodDescriptor, InvokesModel invokesModel) {
        MethodDescriptor invokingMethodDescriptor = methodCache.findOrCreate(invokesModel.getMethodId());
        InvokesDescriptor invokesDescriptor = store.create(InvokesDescriptor.class);

        invokingMethodDescriptor.getInvokes().add(invokesDescriptor);
        invokesDescriptor.setInvokedMethod(invokedMethodDescriptor);

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
