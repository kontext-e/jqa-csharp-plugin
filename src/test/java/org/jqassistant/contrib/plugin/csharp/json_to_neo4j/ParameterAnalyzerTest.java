package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ParameterModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MethodDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ParameterDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.TypeDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ParameterAnalyzerTest {


    private Store store;
    private ParameterAnalyzer parameterAnalyzer;
    private TypeCache typeCache;

    @BeforeEach
    void setup(){
        typeCache = mock();
        store = mock();
        parameterAnalyzer = new ParameterAnalyzer(typeCache, store);
    }

    @Test
    void TestAddOneParameter(){
        MethodModel methodModel = new MethodModel();
        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();
        ParameterModel parameterModel = new ParameterModel();
        parameterModel.setName("Param1");
        parameterModel.setType("int");
        methodModel.setParameters(toList(parameterModel));

        ParameterDescriptorImpl parameterDescriptor = new ParameterDescriptorImpl();
        when(store.create(ParameterDescriptor.class)).thenReturn(parameterDescriptor);
        TypeDescriptorImpl type = new TypeDescriptorImpl("Type");
        when(typeCache.findOrCreate(eq("int"))).thenReturn(type);

        parameterAnalyzer.addParameters(methodModel, methodDescriptor);

        verify(store).create(ParameterDescriptor.class);
        assertThat(methodDescriptor.getParameters().size()).isEqualTo(1);
        ParameterDescriptor result = methodDescriptor.getParameters().get(0);
        assertThat(result.getIndex()).isEqualTo(0);
        assertThat(result.getType()).isEqualTo(type);
        assertThat(result.getName()).isEqualTo("Param1");
    }

    private <T> List<T> toList(T item){
        List<T> list = new ArrayList<>();
        list.add(item);
        return list;
    }


}
