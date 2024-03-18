package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.UsingModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.CSharpFileDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ClassDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.InterfaceDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.NamespaceDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.TypeDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.UsesNamespaceDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DependencyAnalyzerTest {

    private Store store;
    private CSharpFileCache fileCache;
    private NamespaceCache namespaceCache;
    private TypeCache typeCache;
    private DependencyAnalyzer dependencyAnalyzer;

    @BeforeEach
    void setup(){
        store = mock();
        fileCache = mock();
        namespaceCache = mock();
        typeCache = mock();
        dependencyAnalyzer = new DependencyAnalyzer(fileCache, namespaceCache, typeCache, store);
    }

    @Test
    void testCreateUsings(){
        UsingModel usingModel = new UsingModel();
        usingModel.setName("UsingModel");
        usingModel.setAlias("Alias");
        FileModel fileModel = new FileModel();
        fileModel.setUsings(toList(usingModel));
        fileModel.setAbsolutePath("Absolute.Path");

        CSharpFileDescriptorImpl cSharpFileDescriptor = new CSharpFileDescriptorImpl("Name", "FileName");
        when(fileCache.get(anyString())).thenReturn(cSharpFileDescriptor);
        NamespaceDescriptor namespaceDescriptor = new NamespaceDescriptorImpl();
        when(namespaceCache.findOrCreate("UsingModel")).thenReturn(namespaceDescriptor);
        UsesNamespaceDescriptorImpl usesNamespaceDescriptor = new UsesNamespaceDescriptorImpl();
        when(store.create(any(CSharpFileDescriptor.class), eq(UsesNamespaceDescriptor.class), any(NamespaceDescriptor.class))).thenReturn(usesNamespaceDescriptor);

        dependencyAnalyzer.createUsings(fileModel);

        verify(store).create(eq(cSharpFileDescriptor), eq(UsesNamespaceDescriptor.class), eq(namespaceDescriptor));
        assertThat(usesNamespaceDescriptor.getAlias()).isEqualTo("Alias");
    }

    @Test
    void testLinkBaseTypes(){
        ClassModel classModel = new ClassModel();
        classModel.setBaseType("BaseType");
        ClassDescriptor classDescriptor = new ClassDescriptorImpl("BaseClass");
        when(typeCache.findAny(any())).thenReturn(classDescriptor);
        TypeDescriptor typeDescriptor = new TypeDescriptorImpl("BaseClass");
        when(typeCache.findOrCreate(any())).thenReturn(typeDescriptor);

        dependencyAnalyzer.linkBaseTypes(classModel);

        verify(typeCache).findOrCreate("BaseType");
        assertThat(classDescriptor.getSuperClass()).isEqualTo(typeDescriptor);
    }

    @Test
    void testLinkBaseTypesNoBaseTypes(){
        ClassModel classModel = new ClassModel();
        ClassDescriptor classDescriptor = new ClassDescriptorImpl("BaseClass");
        when(typeCache.findAny(any())).thenReturn(classDescriptor);

        dependencyAnalyzer.linkBaseTypes(classModel);

        verify(typeCache, never()).findOrCreate("BaseType");
        assertThat(classDescriptor.getSuperClass()).isNull();
    }

    @Test
    void testLinkInterfacesForClasses(){
        ClassModel classModel = new ClassModel();
        List<String> implementedInterfaces = Arrays.asList("Interface1", "Interface2", "Interface3");
        classModel.setImplementedInterfaces(implementedInterfaces);
        FileModel fileModel = new FileModel();
        fileModel.setClasses(Collections.singletonList(classModel));
        fileModel.setInterfaces(new ArrayList<>());

        ClassDescriptor classDescriptor = new ClassDescriptorImpl("class");
        TypeDescriptor typeDescriptor1 =  new TypeDescriptorImpl("Interface1");
        TypeDescriptor typeDescriptor2 =  new TypeDescriptorImpl("Interface2");
        TypeDescriptor typeDescriptor3 =  new TypeDescriptorImpl("Interface3");

        when(typeCache.findOrCreate(anyString())).thenReturn(typeDescriptor1, typeDescriptor2, typeDescriptor3);
        when(typeCache.findAny(any())).thenReturn(classDescriptor);

        dependencyAnalyzer.linkInterfaces(fileModel);

        verify(typeCache, times(3)).findOrCreate(anyString());
        assertThat(classDescriptor.getInterfaces().size()).isEqualTo(3);
        List<TypeDescriptor> interfaces = classDescriptor.getInterfaces();
        assertThat(interfaces.contains(typeDescriptor1)).isTrue();
        assertThat(interfaces.contains(typeDescriptor2)).isTrue();
        assertThat(interfaces.contains(typeDescriptor3)).isTrue();
    }

    @Test
    void testLinkInterfacesNoImplementedInterfaces(){
        ClassModel classModel = new ClassModel();
        classModel.setImplementedInterfaces(new ArrayList<>());
        InterfaceModel interfaceModel = new InterfaceModel();
        interfaceModel.setImplementedInterfaces(new ArrayList<>());
        FileModel fileModel = new FileModel();
        fileModel.setClasses(Collections.singletonList(classModel));
        fileModel.setInterfaces(Collections.singletonList(interfaceModel));

        dependencyAnalyzer.linkInterfaces(fileModel);

        verify(typeCache, never()).findOrCreate(anyString());

    }

    @Test
    void testLinkInterfacesForInterfaces(){
        InterfaceModel interfaceModel = new InterfaceModel();
        List<String> implementedInterfaces = Arrays.asList("Interface1", "Interface2", "Interface3");
        interfaceModel.setImplementedInterfaces(implementedInterfaces);
        FileModel fileModel = new FileModel();
        fileModel.setClasses(new ArrayList<>());
        fileModel.setInterfaces(Collections.singletonList(interfaceModel));

        InterfaceDescriptorImpl interfaceDescriptor = new InterfaceDescriptorImpl("interface");
        TypeDescriptor typeDescriptor1 =  new InterfaceDescriptorImpl("Interface1");
        TypeDescriptor typeDescriptor2 =  new InterfaceDescriptorImpl("Interface2");
        TypeDescriptor typeDescriptor3 =  new InterfaceDescriptorImpl("Interface3");

        when(typeCache.findOrCreate(anyString())).thenReturn(typeDescriptor1, typeDescriptor2, typeDescriptor3);
        when(typeCache.findAny(any())).thenReturn(interfaceDescriptor);

        dependencyAnalyzer.linkInterfaces(fileModel);

        verify(typeCache, times(3)).findOrCreate(anyString());
        assertThat(interfaceDescriptor.getInterfaces().size()).isEqualTo(3);
        List<TypeDescriptor> interfaces = interfaceDescriptor.getInterfaces();
        assertThat(interfaces.contains(typeDescriptor1)).isTrue();
        assertThat(interfaces.contains(typeDescriptor2)).isTrue();
        assertThat(interfaces.contains(typeDescriptor3)).isTrue();
    }



    private <T> List<T> toList(T item){
        List<T> list = new ArrayList<>();
        list.add(item);
        return list;
    }

}
