package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InvokesModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberAccessModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberAccessDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;

import java.util.Optional;

public class InvocationAnalyzer {

    private final Store store;
    private final MethodCache methodCache;
    private final PropertyCache propertyCache;

    public InvocationAnalyzer(Store store, MethodCache methodCache, PropertyCache propertyCache) {
        this.store = store;
        this.methodCache = methodCache;
        this.propertyCache = propertyCache;
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

    protected void addPropertyAccesses(MethodModel methodModel){
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
