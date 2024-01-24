package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.FieldCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FieldModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ClassDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.FieldDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.PrimitiveValueDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.TypeDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PrimitiveValueDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MemberAnalyzerTest {

    Store store;
    FieldCache fieldCache;
    TypeCache typeCache;
    private MemberAnalyzer memberAnalyzer;

    @BeforeEach
    void setup(){
        store = mock();
        fieldCache = mock();
        typeCache = mock();
        memberAnalyzer = new MemberAnalyzer(store, fieldCache, typeCache);
    }

    @Test
    void testCreateSingleField(){
        ClassModel classModel = createClassModel("Class", createFields(1));
        when(fieldCache.create(any())).thenReturn(new FieldDescriptorImpl());
        when(typeCache.findOrCreate(any())).thenReturn(new TypeDescriptorImpl("TypeOfField"));

        ClassDescriptorImpl containingClass = new ClassDescriptorImpl("ContainingClass");
        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.of(containingClass));
        when(store.create(PrimitiveValueDescriptor.class)).thenReturn(new PrimitiveValueDescriptorImpl());

        memberAnalyzer.createFields(classModel, "Relative.Path");

        verify(store, never()).create(PrimitiveValueDescriptor.class);
        verify(fieldCache).create("fqn.Field0");
        assertThat(containingClass.getDeclaredMembers().size()).isEqualTo(1);
        MemberDescriptor memberDescriptor = containingClass.getDeclaredMembers().get(0);
        assertThat(memberDescriptor).isInstanceOfAny(FieldDescriptor.class);
        FieldDescriptor fieldDescriptor = (FieldDescriptor) memberDescriptor;
        assertFieldDescriptorHasBeenFilled(fieldDescriptor);
        assertThat(fieldDescriptor.getValue()).isNull();
    }

    @Test
    void testCreateNoFields(){
        ClassModel classModel = createClassModel("Class", createFields(0));
        ClassDescriptorImpl containingClass = new ClassDescriptorImpl("ContainingClass");
        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.of(containingClass));

        memberAnalyzer.createFields(classModel, "Relative.Path");

        verify(store, never()).create(any());
        assertThat(containingClass.getDeclaredMembers().isEmpty()).isTrue();
    }

    @Test
    void testCreateFieldsForNonexistentType(){
        ClassModel classModel = createClassModel("Class", createFields(0));
        ClassDescriptorImpl containingClass = new ClassDescriptorImpl("ContainingClass");
        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.empty());

        memberAnalyzer.createFields(classModel, "Relative.Path");

        verify(store, never()).create(any());
        verify(fieldCache, never()).create(any());
        assertThat(containingClass.getDeclaredMembers().isEmpty()).isTrue();
    }

    @Test
    void testCreateFieldWithDefaultValue(){
        List<FieldModel> fields = createFields(1);
        fields.get(0).setConstantValue("ConstantValue");
        ClassModel classModel = createClassModel("Class", fields);
        when(fieldCache.create(any())).thenReturn(new FieldDescriptorImpl());
        when(typeCache.findOrCreate(any())).thenReturn(new TypeDescriptorImpl("TypeOfField"));

        ClassDescriptorImpl containingClass = new ClassDescriptorImpl("ContainingClass");
        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.of(containingClass));
        when(store.create(PrimitiveValueDescriptor.class)).thenReturn(new PrimitiveValueDescriptorImpl());

        memberAnalyzer.createFields(classModel, "Relative.Path");

        verify(store).create(PrimitiveValueDescriptor.class);
        verify(fieldCache).create("fqn.Field0");
        assertThat(containingClass.getDeclaredMembers().size()).isEqualTo(1);
        MemberDescriptor memberDescriptor = containingClass.getDeclaredMembers().get(0);
        assertThat(memberDescriptor).isInstanceOfAny(FieldDescriptor.class);
        FieldDescriptor fieldDescriptor = (FieldDescriptor) memberDescriptor;
        assertFieldDescriptorHasBeenFilled(fieldDescriptor);

        assertThat(fieldDescriptor.getValue()).isInstanceOfAny(PrimitiveValueDescriptor.class);
        assertThat(fieldDescriptor.getValue().getValue()).isEqualTo("ConstantValue");
    }

    private static void assertFieldDescriptorHasBeenFilled(FieldDescriptor fieldDescriptor) {
        assertThat(fieldDescriptor.getFullQualifiedName()).isNotNull();
        assertThat(fieldDescriptor.getName()).isNotNull();
        assertThat(fieldDescriptor.getAccessibility()).isNotNull();
        assertThat(fieldDescriptor.isVolatile()).isTrue();
        assertThat(fieldDescriptor.isStatic()).isTrue();
        assertThat(fieldDescriptor.getType().getName()).isEqualTo("TypeOfField");
    }

    private ClassModel createClassModel(String name, List<FieldModel> fields){
        ClassModel classModel = new ClassModel();
        classModel.setName(name);
        classModel.setFields(fields);

        return classModel;
    }

    private List<FieldModel> createFields(int amount) {
        List<FieldModel> fieldModels = new ArrayList<>();
        for (int i = 0; i<amount; i++){
            FieldModel fieldModel = new FieldModel();
            fillFieldModel(fieldModel, i);
            fieldModels.add(fieldModel);
        }
        return fieldModels;
    }

    private void fillFieldModel(FieldModel fieldModel, int seed) {
        fieldModel.setFqn("fqn.Field"+seed);
        fieldModel.setName("Field"+seed);
        fieldModel.setAccessibility("private");
        fieldModel.setVolatileKeyword(true);
        fieldModel.setSealed(true);
        fieldModel.setStaticKeyword(true);
    }

}
