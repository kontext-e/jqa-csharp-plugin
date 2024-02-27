package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InvokesModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;

public class InvocationAnalyzer {

    private final Store store;
    private final MethodCache methodCache;

    public InvocationAnalyzer(Store store, MethodCache methodCache) {
        this.store = store;
        this.methodCache = methodCache;
    }

    protected void addInvocations(MethodModel methodModel) {
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
}
