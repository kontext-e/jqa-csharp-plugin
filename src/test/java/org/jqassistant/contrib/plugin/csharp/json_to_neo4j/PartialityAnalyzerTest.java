package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MethodDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.TypeDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PartialityAnalyzerTest {

    private MethodCache methodCacheMock;
    private TypeCache typeCacheMock;
    private PartialityAnalyzer partialityAnalyzer;

    @BeforeEach
    void setUp() {
        methodCacheMock = mock();
        typeCacheMock = mock();
        partialityAnalyzer = new PartialityAnalyzer(methodCacheMock, typeCacheMock);
    }

    @Test
    void linkPartialMethods() {
        List<List<MethodDescriptor>> methodList = createMethodDescriptors();
        when(methodCacheMock.findAllPartialMethods()).thenReturn(methodList);

        partialityAnalyzer.linkPartialMethods();

        assertThat(methodList.get(0).get(0).getMethodFragments().size()).isEqualTo(1);
        assertThat(methodList.get(0).get(1).getMethodFragments().size()).isEqualTo(0);
        assertThat(methodList.get(1).get(0).getMethodFragments().size()).isEqualTo(0);

        assertThat(methodList.get(0).get(0).getMethodFragments().get(0)).isEqualTo(methodList.get(0).get(1));
    }

    private List<List<MethodDescriptor>> createMethodDescriptors() {
        MethodDescriptor partialMethodImplementation = new MethodDescriptorImpl();
        MethodDescriptor partialMethodDeclaration = new MethodDescriptorImpl();
        MethodDescriptor nonPartialMethod = new MethodDescriptorImpl();
        partialMethodImplementation.setIsImplementation(true);
        partialMethodDeclaration.setIsImplementation(false);

        List<MethodDescriptor> partialMethodList = new ArrayList<>();
        partialMethodList.add(partialMethodImplementation);
        partialMethodList.add(partialMethodDeclaration);

        List<MethodDescriptor> nonPartialMethodList = new ArrayList<>();
        nonPartialMethodList.add(nonPartialMethod);

        List<List<MethodDescriptor>> methodList = new ArrayList<>();
        methodList.add(partialMethodList);
        methodList.add(nonPartialMethodList);

        return methodList;
    }

    @Test
    void linkPartialClasses() {
        List<List<TypeDescriptor>> typeDescriptors = createTypeDescriptors();
        when(typeCacheMock.findAllPartialClasses()).thenReturn(typeDescriptors);

        partialityAnalyzer.linkPartialClasses();

        assertThat(typeDescriptors.get(0).get(0).getClassFragments().size()).isEqualTo(1);
        assertThat(typeDescriptors.get(0).get(1).getClassFragments().size()).isEqualTo(1);
        assertThat(typeDescriptors.get(1).get(0).getClassFragments().size()).isEqualTo(0);

        assertThat(typeDescriptors.get(0).get(0).getClassFragments().get(0)).isEqualTo(typeDescriptors.get(0).get(1));
        assertThat(typeDescriptors.get(0).get(1).getClassFragments().get(0)).isEqualTo(typeDescriptors.get(0).get(0));
    }

    private List<List<TypeDescriptor>> createTypeDescriptors(){
        TypeDescriptor partialTypeDescriptor1 = new TypeDescriptorImpl("partialType");
        TypeDescriptor partialTypeDescriptor2 = new TypeDescriptorImpl("partialType");

        TypeDescriptor nonPartialType = new TypeDescriptorImpl("nonPartialType");

        List<TypeDescriptor> partialTypes = new ArrayList<>();
        partialTypes.add(partialTypeDescriptor1);
        partialTypes.add(partialTypeDescriptor2);

        List<TypeDescriptor> nonPartialTypes = new ArrayList<>();
        nonPartialTypes.add(nonPartialType);

        List<List<TypeDescriptor>> typeList = new ArrayList<>();
        typeList.add(partialTypes);
        typeList.add(nonPartialTypes);

        return typeList;

    }

}