package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InvokesModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.InvokesDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MethodDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InvocationAnalyzerTest {

    private Store store;
    private MethodCache methodCache;
    private InvocationAnalyzer invocationAnalyzer;
    private TypeCache typeCache;

    @BeforeEach
    void setup(){
        store = mock();
        methodCache = mock();
        typeCache = mock();
        invocationAnalyzer = new InvocationAnalyzer(store, methodCache, typeCache);
    }

    @Test
    void testMethodNoInvocations(){
        MethodModel methodModel = new MethodModel();
        methodModel.setInvokedBy(new ArrayList<>());
        methodModel.setInvokes(new ArrayList<>());
        methodModel.setCreatesArrays(new ArrayList<>());

        invocationAnalyzer.analyzeInvocations(methodModel);

        verify(store, never()).create(any());
        verify(methodCache, never()).findAny(any());
        verify(methodCache, never()).findOrCreate(any());
    }

    @Test
    void testMethodInvocations(){
        InvokesModel invokesModel = new InvokesModel();
        invokesModel.setLineNumber(3);
        invokesModel.setMethodId("Some.Method.ID");
        MethodModel methodModel = new MethodModel();
        methodModel.setInvokes(new ArrayList<>());
        methodModel.setCreatesArrays(new ArrayList<>());
        methodModel.setInvokedBy(Collections.singletonList(invokesModel));

        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();
        when(methodCache.findAny(anyString())).thenReturn(methodDescriptor);
        MethodDescriptor invokedMethodDescriptor = new MethodDescriptorImpl();
        when(methodCache.findOrCreate(any())).thenReturn(invokedMethodDescriptor);
        InvokesDescriptor invokesDescriptor = new InvokesDescriptorImpl();
        when(store.create(any(), eq(InvokesDescriptor.class), any())).thenReturn(invokesDescriptor);

        invocationAnalyzer.analyzeInvocations(methodModel);

        verify(methodCache).findAny(any());
        verify(methodCache).findOrCreate(any());
        verify(store).create(any(), eq(InvokesDescriptor.class), any());
        assertThat(invokesDescriptor.getLineNumber()).isEqualTo(3);
        //Testing for RelationDescriptors such as InvokesDescriptor.class seems to be difficult or impossible
    }
}
