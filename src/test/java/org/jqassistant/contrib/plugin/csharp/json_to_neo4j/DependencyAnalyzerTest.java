package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.UsingModel;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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

    private <T> List<T> toList(T item){
        List<T> list = new ArrayList<>();
        list.add(item);
        return list;
    }

}
