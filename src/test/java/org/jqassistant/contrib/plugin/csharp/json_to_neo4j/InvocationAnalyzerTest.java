package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InvokesModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberAccessModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.InvokesDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MemberAccessDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MethodDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.PropertyDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberAccessDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
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

    private Store store;
    private MethodCache methodCache;
    private PropertyCache propertyCache;
    private InvocationAnalyzer invocationAnalyzer;

    @BeforeEach
    void setup(){
        store = mock();
        methodCache = mock();
        propertyCache = mock();
        invocationAnalyzer = new InvocationAnalyzer(store, methodCache, propertyCache);
    }

    @Test
    void testMethodNoInvocations(){
        MethodModel methodModel = new MethodModel();
        methodModel.setInvocations(new ArrayList<>());

        invocationAnalyzer.addInvocations(methodModel);

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
        methodModel.setInvocations(Collections.singletonList(invokesModel));

        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();
        when(methodCache.findAny(anyString())).thenReturn(methodDescriptor);
        MethodDescriptor invokedMethodDescriptor = new MethodDescriptorImpl();
        when(methodCache.findOrCreate(any())).thenReturn(invokedMethodDescriptor);
        InvokesDescriptor invokesDescriptor = new InvokesDescriptorImpl();
        when(store.create(any(), eq(InvokesDescriptor.class), any())).thenReturn(invokesDescriptor);

        invocationAnalyzer.addInvocations(methodModel);

        verify(methodCache).findAny(any());
        verify(methodCache).findOrCreate(any());
        verify(store).create(any(), eq(InvokesDescriptor.class), any());
        assertThat(invokesDescriptor.getLineNumber()).isEqualTo(3);
        //Testing for RelationDescriptors such as InvokesDescriptor.class seems to be difficult or impossible
    }

    @Test
    void testPropertyAccesses(){
        MemberAccessModel memberAccessModel = new MemberAccessModel();
        memberAccessModel.setLineNumber(8);
        memberAccessModel.setMemberId("Some.Property.ID");
        MethodModel methodModel = new MethodModel();
        methodModel.setMemberAccesses(Collections.singletonList(memberAccessModel));

        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();
        when(methodCache.findAny(anyString())).thenReturn(methodDescriptor);
        PropertyDescriptor property = new PropertyDescriptorImpl();
        when(propertyCache.getPropertyFromSubstring(any())).thenReturn(Optional.of(property));
        MemberAccessDescriptor memberAccessDescriptor = new MemberAccessDescriptorImpl();
        when(store.create(any(), eq(MemberAccessDescriptor.class), any())).thenReturn(memberAccessDescriptor);

        invocationAnalyzer.addPropertyAccesses(methodModel);

        verify(methodCache).findAny(any());
        verify(propertyCache).getPropertyFromSubstring(any());
        verify(store).create(any(), eq(MemberAccessDescriptor.class), any());
        assertThat(memberAccessDescriptor.getLineNumber()).isEqualTo(8);
        //Testing for RelationDescriptors such as MemberAccessDescriptor.class seems to be difficult or impossible
    }

}
