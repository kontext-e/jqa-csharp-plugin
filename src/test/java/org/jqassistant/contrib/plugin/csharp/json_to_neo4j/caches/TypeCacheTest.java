package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.EnumModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.TypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ClassDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.EnumDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.InterfaceDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.TypeDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TypeCacheTest {

    private TypeCache typeCache;
    private Store mockStore;

    @BeforeEach
    void setup() {
        mockStore = createMockStore();
        typeCache = new TypeCache(mockStore);
    }

    private Store createMockStore() {
        Store store = mock();
        when(store.create(ClassDescriptor.class)).thenReturn(new ClassDescriptorImpl("Class"));
        when(store.create(InterfaceTypeDescriptor.class)).thenReturn(new InterfaceDescriptorImpl("Interface"));
        when(store.create(EnumTypeDescriptor.class)).thenReturn(new EnumDescriptorImpl("Enum"));
        when(store.create(TypeDescriptor.class)).thenReturn(new TypeDescriptorImpl("Type"));
        return store;
    }

    @Test
    void testCreateClass() {
        ClassModel classModel = new ClassModel();
        classModel.setFqn("Class1");

        TypeDescriptor typeDescriptor = typeCache.create(classModel);

        verify(mockStore).create(ClassDescriptor.class);
        assertThat(typeDescriptor instanceof ClassDescriptor).isTrue();
        assertThat(typeDescriptor.getFullQualifiedName()).isEqualTo("Class1");
        assertThat(typeCache.findAll("Class1").size()).isEqualTo(1);
    }

    @Test
    void testCreateInterfaces() {
        InterfaceModel interfaceModel = new InterfaceModel();
        interfaceModel.setFqn("Interface1");

        TypeDescriptor typeDescriptor = typeCache.create(interfaceModel);

        verify(mockStore).create(InterfaceTypeDescriptor.class);
        assertThat(typeDescriptor instanceof InterfaceTypeDescriptor).isTrue();
        assertThat(typeDescriptor.getFullQualifiedName()).isEqualTo("Interface1");
        assertThat(typeCache.findAll("Interface1").size()).isEqualTo(1);
    }

    @Test
    void testCreateEnums() {
        EnumModel enumModel = new EnumModel();
        enumModel.setFqn("Enum1");

        TypeDescriptor typeDescriptor = typeCache.create(enumModel);

        verify(mockStore).create(EnumTypeDescriptor.class);
        assertThat(typeDescriptor instanceof EnumTypeDescriptor).isTrue();
        assertThat(typeDescriptor.getFullQualifiedName()).isEqualTo("Enum1");
        assertThat(typeCache.findAll("Enum1").size()).isEqualTo(1);
    }

    @Test
    void testCreateType() {
        TypeModel typeModel = new TypeModel();
        typeModel.setFqn("Type1");

        TypeDescriptor typeDescriptor = typeCache.create(typeModel);

        verify(mockStore).create(TypeDescriptor.class);
        assertThat(!(typeDescriptor instanceof ClassDescriptor) && !(typeDescriptor instanceof InterfaceTypeDescriptor)).isTrue();
        assertThat(typeDescriptor.getFullQualifiedName()).isEqualTo("Type1");
        assertThat(typeCache.findAll("Type1").size()).isEqualTo(1);
    }

    @Test
    void testCreateMultipleClasses() {
        ClassModel classModel1 = new ClassModel();
        classModel1.setFqn("PartialClass");
        ClassModel classModel2 = new ClassModel();
        classModel2.setFqn("PartialClass");

        typeCache.create(classModel1);
        typeCache.create(classModel2);

        assertThat(typeCache.findAll("PartialClass").size()).isEqualTo(2);
    }

    @Test
    void testFindOrCreateCreate() {
        TypeDescriptor typeDescriptor = typeCache.findOrCreate("NonExistingClass");

        assertThat(typeDescriptor).isNotNull();
        assertThat(typeDescriptor.getFullQualifiedName()).isEqualTo("NonExistingClass");
    }

    @Test
    void testFindOrCreate() {
        ClassModel classModel = new ClassModel();
        classModel.setFqn("Class1");
        TypeDescriptor typeDescriptor = typeCache.create(classModel);

        TypeDescriptor result = typeCache.findOrCreate("Class1");

        assertThat(result).isEqualTo(typeDescriptor);
    }

    @Test
    void testFindAllPartialClasses(){
        ClassModel classModel11 = new ClassModel();
        classModel11.setFqn("PartialClass1");
        ClassModel classModel12 = new ClassModel();
        classModel12.setFqn("PartialClass1");
        ClassModel classModel21 = new ClassModel();
        classModel21.setFqn("PartialClass2");
        ClassModel classModel22 = new ClassModel();
        classModel22.setFqn("PartialClass2");
        ClassModel nonPartialClassModel = new ClassModel();
        nonPartialClassModel.setFqn("nonPartialClass");

        TypeDescriptor typeDescriptor11 = typeCache.create(classModel11);
        TypeDescriptor typeDescriptor12 = typeCache.create(classModel12);
        TypeDescriptor typeDescriptor21 = typeCache.create(classModel21);
        TypeDescriptor typeDescriptor22 = typeCache.create(classModel22);
        TypeDescriptor nonPartialTypeDescriptor = typeCache.create(nonPartialClassModel);

        List<List<TypeDescriptor>> allPartialClasses = typeCache.findAllPartialClasses();

        assertThat(allPartialClasses.size()).isEqualTo(2);
        assertThat(allPartialClasses.get(0).size()).isEqualTo(2);
        assertThat(allPartialClasses.get(1).size()).isEqualTo(2);
        assertThat(allPartialClasses.get(0)).contains(typeDescriptor11);
        assertThat(allPartialClasses.get(0)).contains(typeDescriptor12);
        assertThat(allPartialClasses.get(1)).contains(typeDescriptor21);
        assertThat(allPartialClasses.get(1)).contains(typeDescriptor22);

    }


}
