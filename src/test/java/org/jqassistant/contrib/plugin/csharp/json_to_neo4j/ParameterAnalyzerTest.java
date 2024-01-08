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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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
        ParameterModel parameterModel = createParameterModel("Param1", "int");
        List<ParameterModel> parameterModels = new ArrayList<>();
        parameterModels.add(parameterModel);
        methodModel.setParameters(parameterModels);

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

    @Test
    void TestAddMultipleParameters(){
        MethodModel methodModel = new MethodModel();
        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();
        ParameterModel parameterModel1 = createParameterModel("Param1", "int");
        ParameterModel parameterModel2 = createParameterModel("Param2", "string");
        List<ParameterModel> parameterModels = new ArrayList<>();
        parameterModels.add(parameterModel1);
        parameterModels.add(parameterModel2);
        methodModel.setParameters(parameterModels);

        ParameterDescriptorImpl parameterDescriptor1 = new ParameterDescriptorImpl();
        ParameterDescriptorImpl parameterDescriptor2 = new ParameterDescriptorImpl();
        when(store.create(ParameterDescriptor.class)).thenReturn(parameterDescriptor1, parameterDescriptor2);
        TypeDescriptorImpl typeInt = new TypeDescriptorImpl("TypeInt");
        when(typeCache.findOrCreate(eq("int"))).thenReturn(typeInt);
        TypeDescriptorImpl typeString = new TypeDescriptorImpl("TypeString");
        when(typeCache.findOrCreate(eq("string"))).thenReturn(typeString);

        parameterAnalyzer.addParameters(methodModel, methodDescriptor);

        verify(store, times(2)).create(ParameterDescriptor.class);
        assertThat(methodDescriptor.getParameters().size()).isEqualTo(2);
        assertThat(methodDescriptor.getParameters().get(0).getName()).isEqualTo("Param1");
        assertThat(methodDescriptor.getParameters().get(1).getName()).isEqualTo("Param2");
    }

    @Test
    void TestAddZeroParameters(){
        MethodModel methodModel = new MethodModel();
        methodModel.setParameters(new ArrayList<>());
        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();

        parameterAnalyzer.addParameters(methodModel, methodDescriptor);

        verify(store, never()).create(ParameterDescriptor.class);
        assertThat(methodDescriptor.getParameters().size()).isEqualTo(0);
    }

    private static ParameterModel createParameterModel(String name, String type) {
        ParameterModel parameterModel = new ParameterModel();
        parameterModel.setName(name);
        parameterModel.setType(type);
        return parameterModel;
    }


}
