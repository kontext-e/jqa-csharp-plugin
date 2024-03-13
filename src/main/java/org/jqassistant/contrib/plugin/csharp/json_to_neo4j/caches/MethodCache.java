package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MethodCache {

    private final Store store;
    private final HashMap<String, List<MethodDescriptor>> cache;

    public MethodCache(Store store) {
        this.store = store;
        this.cache = new HashMap<>();
    }

    public Optional<MethodDescriptor> findAny(String fqn) {
        List<MethodDescriptor> methodDescriptors = cache.get(fqn);
        if (methodDescriptors == null) return Optional.empty();
        Optional<MethodDescriptor> methodDescriptor = methodDescriptors.stream().filter(MethodDescriptor::isImplementation).findAny();
        return Optional.of(methodDescriptor.orElseGet(() -> methodDescriptors.get(0)));
    }

    public <T extends MethodDescriptor> MethodDescriptor create(String fqn, Class<T> descriptorClass) {
        if (cache.containsKey(fqn) && descriptorClass.equals(ConstructorDescriptor.class))
            return cache.get(fqn).get(0);

        T descriptor = store.create(descriptorClass);
        descriptor.setFullQualifiedName(fqn);

        if (cache.containsKey(fqn)){
            cache.get(fqn).add(descriptor);
        } else {
            List<MethodDescriptor> newList = new ArrayList<>();
            newList.add(descriptor);
            cache.put(fqn, newList);
        }

        return descriptor;
    }

    public MethodDescriptor findOrCreate(String fqn) {
        if (cache.containsKey(fqn)) {
            return findAny(fqn).get();
        }
        return create(fqn, MethodDescriptor.class);
    }

    public List<List<MethodDescriptor>> findAllPartialMethods(){
        return cache.values()
                    .stream()
                    .filter(list -> list.size() > 1)
                    .collect(Collectors.toList());
    }
}
