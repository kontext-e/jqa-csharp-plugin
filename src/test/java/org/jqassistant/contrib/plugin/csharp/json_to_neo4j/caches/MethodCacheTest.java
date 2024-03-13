package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MethodDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class MethodCacheTest {

    private Store mockStore;
    private MethodCache methodCache;

    @BeforeEach
    void setup(){
        mockStore = mock();
        methodCache = new MethodCache(mockStore);
    }

    @Test
    void testCreateNew(){
        MethodDescriptorImpl methodDescriptor = new MethodDescriptorImpl();
        when(mockStore.create(any())).thenReturn(methodDescriptor);

        MethodDescriptor result = methodCache.create("Fqn.Method", MethodDescriptor.class);

        assertThat(result instanceof ConstructorDescriptor).isFalse();

        Optional<MethodDescriptor> methodDescriptor1 = methodCache.findAny("Fqn.Method");
        assertThat(methodDescriptor1.isPresent()).isTrue();
        assertThat(methodDescriptor1.get()).isEqualTo(methodDescriptor);
        verify(mockStore, times(1)).create(MethodDescriptor.class);
    }

    @Test
    void testCreateExisting(){
        MethodDescriptorImpl methodDescriptor1 = new MethodDescriptorImpl();
        MethodDescriptorImpl methodDescriptor2 = new MethodDescriptorImpl();
        when(mockStore.create(any())).thenReturn(methodDescriptor1, methodDescriptor2);

        MethodDescriptor result = methodCache.create("Fqn.Method", MethodDescriptor.class);
        MethodDescriptor newResult = methodCache.create("Fqn.Method", MethodDescriptor.class);
        result.setImplementation(true);

        assertThat(newResult.equals(result)).isFalse();
        Optional<MethodDescriptor> methodDescriptor = methodCache.findAny("Fqn.Method");
        assertThat(methodDescriptor.isPresent()).isTrue();
        assertThat(methodDescriptor.get()).isEqualTo(result);
        verify(mockStore, times(2)).create(MethodDescriptor.class);
    }

    @Test
    void testFindOrCreate(){
        MethodDescriptorImpl methodDescriptor1 = new MethodDescriptorImpl();
        MethodDescriptorImpl methodDescriptor2 = new MethodDescriptorImpl();
        when(mockStore.create(any())).thenReturn(methodDescriptor1, methodDescriptor2);
        MethodDescriptor existingDescriptor = methodCache.create("Method1", MethodDescriptor.class);

        MethodDescriptor existingMethod = methodCache.findOrCreate("Method1");
        MethodDescriptor nonExistentMethod = methodCache.findOrCreate("Method2");

        assertThat(existingMethod).isEqualTo(existingDescriptor);
        assertThat(nonExistentMethod).isNotEqualTo(existingDescriptor);
        verify(mockStore, times(2)).create(MethodDescriptor.class);
    }

    @Test
    void testFindAllPartialMethods(){
        MethodDescriptorImpl methodDescriptor1 = new MethodDescriptorImpl();
        MethodDescriptorImpl methodDescriptor2 = new MethodDescriptorImpl();
        MethodDescriptorImpl methodDescriptor3 = new MethodDescriptorImpl();
        MethodDescriptorImpl methodDescriptor4 = new MethodDescriptorImpl();
        MethodDescriptorImpl methodDescriptor5 = new MethodDescriptorImpl();
        when(mockStore.create(any())).thenReturn(methodDescriptor1, methodDescriptor2, methodDescriptor3, methodDescriptor4, methodDescriptor5);
        MethodDescriptor methodDescriptor11 = methodCache.create("Method1", MethodDescriptor.class);
        MethodDescriptor methodDescriptor12 = methodCache.create("Method1", MethodDescriptor.class);
        MethodDescriptor methodDescriptor21 = methodCache.create("Method2", MethodDescriptor.class);
        MethodDescriptor methodDescriptor22 = methodCache.create("Method2", MethodDescriptor.class);
        MethodDescriptor nonPartialMethod = methodCache.create("Method3", MethodDescriptor.class);

        List<List<MethodDescriptor>> partialMethods = methodCache.findAllPartialMethods();

        assertThat(partialMethods.size()).isEqualTo(2);
        assertThat(partialMethods.get(0).size()).isEqualTo(2);
        assertThat(partialMethods.get(1).size()).isEqualTo(2);
        assertThat(partialMethods.get(0).contains(methodDescriptor11)).isTrue();
        assertThat(partialMethods.get(0).contains(methodDescriptor12)).isTrue();
        assertThat(partialMethods.get(1).contains(methodDescriptor21)).isTrue();
        assertThat(partialMethods.get(1).contains(methodDescriptor22)).isTrue();
        assertThat(partialMethods.get(0).contains(nonPartialMethod)).isFalse();
        assertThat(partialMethods.get(1).contains(nonPartialMethod)).isFalse();
    }
}
