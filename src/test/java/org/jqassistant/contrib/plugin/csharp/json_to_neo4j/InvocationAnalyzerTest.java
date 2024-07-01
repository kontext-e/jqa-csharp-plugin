package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ArrayCreationModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InvokesModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ArrayCreationDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.InvokesDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MethodDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.TypeDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.ArrayCreationDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvocationAnalyzerTest {

    private final Store store = mock();
    private final MethodCache methodCache = mock();
    private InvocationAnalyzer invocationAnalyzer;
    private final TypeCache typeCache = mock();

    @BeforeEach
    void setup(){
        invocationAnalyzer = new InvocationAnalyzer(store, methodCache, typeCache);
    }

    @Test
    void testMethodNoInvocations(){
        MethodModel methodModel = new MethodModel();
        methodModel.setInvokedBy(new ArrayList<>());
        methodModel.setCreatesArrays(new ArrayList<>());

        invocationAnalyzer.analyzeInvocations(methodModel);

        verify(store, never()).create(any());
        verify(methodCache, never()).findAny(any());
        verify(methodCache, never()).findOrCreate(any());
    }

    @Test
    void testMethodUsages(){
        InvokesModel invokesModel = new InvokesModel();
        invokesModel.setLineNumber(3);
        invokesModel.setMethodId("Some.Method.ID");
        invokesModel.setTypeArguments(new ArrayList<>());
        MethodModel methodModel = new MethodModel();
        methodModel.setFqn("f.q.n");
        methodModel.setCreatesArrays(new ArrayList<>());
        methodModel.setInvokedBy(Collections.singletonList(invokesModel));

        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();
        when(methodCache.findAny(anyString())).thenReturn(Optional.of(methodDescriptor));
        MethodDescriptor invokedMethodDescriptor = new MethodDescriptorImpl();
        when(methodCache.findOrCreate(any())).thenReturn(invokedMethodDescriptor);
        InvokesDescriptor invokesDescriptor = new InvokesDescriptorImpl();
        when(store.create(InvokesDescriptor.class)).thenReturn(invokesDescriptor);

        invocationAnalyzer.analyzeInvocations(methodModel);

        verify(methodCache).findAny(any());
        verify(methodCache).findOrCreate(any());
        verify(store).create(InvokesDescriptor.class);
        assertThat(invokesDescriptor.getLineNumber()).isEqualTo(3);
    }

    @Test
    void testArrayCreation(){
        ArrayCreationModel arrayCreationModel = new ArrayCreationModel();
        arrayCreationModel.setLineNumber(5);
        arrayCreationModel.setType("Some.Type");
        MethodModel methodModel = new MethodModel();
        methodModel.setFqn("f.q.n");
        methodModel.setInvokedBy(new ArrayList<>());
        methodModel.setCreatesArrays(Collections.singletonList(arrayCreationModel));

        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();
        when(methodCache.findAny(anyString())).thenReturn(Optional.of(methodDescriptor));
        TypeDescriptorImpl createdType = new TypeDescriptorImpl("CreatedType");
        when(typeCache.findOrCreate(any())).thenReturn(createdType);
        ArrayCreationDescriptorImpl arrayCreationDescriptor = new ArrayCreationDescriptorImpl();
        when(store.create(any(), eq(ArrayCreationDescriptor.class), any())).thenReturn(arrayCreationDescriptor);

        invocationAnalyzer.analyzeInvocations(methodModel);

        verify(methodCache).findAny(any());
        verify(typeCache).findOrCreate(any());
        verify(store).create(any(), eq(ArrayCreationDescriptor.class), any());
        assertThat(arrayCreationDescriptor.getLineNumber()).isEqualTo(5);
    }
}
