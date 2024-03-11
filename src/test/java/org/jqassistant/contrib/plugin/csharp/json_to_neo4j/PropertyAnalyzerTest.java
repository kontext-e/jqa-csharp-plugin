package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberOwningTypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.PropertyModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ClassDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MethodDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.PropertyDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class PropertyAnalyzerTest {

    private PropertyAnalyzer propertyAnalyzer;
    private TypeCache typeCache;
    private PropertyCache propertyCache;
    private MethodAnalyzer methodAnalyzer;

    @BeforeEach
    void setup(){
        this.typeCache = mock();
        this.propertyCache = mock();
        this.methodAnalyzer = mock();
        this.propertyAnalyzer = new PropertyAnalyzer(typeCache, propertyCache);
        prepareMocks();
    }

    @Test
    void testCreateNoProperties(){
        List<PropertyModel> propertyModels = packagePropertiesToList();
        ClassDescriptorImpl classDescriptor = new ClassDescriptorImpl("Class");
        MemberOwningTypeModel classModel = prepareClassAndFileStructure(propertyModels);

        propertyAnalyzer.createProperties(classModel);

        verifyNoInteractions(propertyCache);
        verify(typeCache, times(1)).findAny(any());
        assertThat(classDescriptor.getDeclaredMembers().size()).isEqualTo(0);
    }

    @Test
    void testCreateProperty(){
        PropertyModel propertyModel = new PropertyModel();
        fillPropertyModel(propertyModel);
        List<PropertyModel> propertyModels = packagePropertiesToList(propertyModel);
        MemberOwningTypeModel classModel = prepareClassAndFileStructure(propertyModels);

        propertyAnalyzer.createProperties(classModel);

        verify(typeCache, times(1)).findOrCreate(anyString());
        verify(typeCache, times(1)).findAny(anyString());
        verify(propertyCache, times(1)).create(anyString());
    }

    private void prepareMocks() {
        PropertyDescriptor propertyDescriptor = new PropertyDescriptorImpl();
        when(propertyCache.create(any())).thenReturn(propertyDescriptor);
        ClassDescriptorImpl classDescriptor = new ClassDescriptorImpl("Class");
        when(typeCache.findAny(any())).thenReturn(classDescriptor);
        MethodDescriptor methodDescriptor = new MethodDescriptorImpl();
        when(methodAnalyzer.createMethodDescriptor(any())).thenReturn(methodDescriptor);
    }

    private void fillPropertyModel(PropertyModel propertyModel) {
        propertyModel.setFqn("Property.Model");
        propertyModel.setName("Model");
        propertyModel.setAccessibility("Public");
        propertyModel.setType("int");
    }

    private MemberOwningTypeModel prepareClassAndFileStructure(List<PropertyModel> propertyModels){
        ClassModel classModel = new ClassModel();
        classModel.setFqn("Another.Class.Model");
        classModel.setProperties(propertyModels);

        return classModel;
    }

    private List<PropertyModel> packagePropertiesToList(PropertyModel... propertyModels){
        return Arrays.asList(propertyModels);
    }

}
