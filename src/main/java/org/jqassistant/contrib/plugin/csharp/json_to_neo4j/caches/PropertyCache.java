package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;

import java.util.HashMap;
import java.util.Optional;

public class PropertyCache {

    private final Store store;
    private final HashMap<String, PropertyDescriptor> cache;

    public PropertyCache(Store store) {
        this.store = store;
        this.cache = new HashMap<>();
    }

    public PropertyDescriptor create(String key) {
        PropertyDescriptor descriptor = store.create(PropertyDescriptor.class);
        cache.put(key, descriptor);

        return descriptor;
    }

    public PropertyDescriptor find(String key){
        return cache.get(key);
    }

    public Optional<PropertyDescriptor> getPropertyFromSubstring(String substring) {
        System.out.println(substring);
        if (substring == null || substring.isEmpty()) return Optional.empty();

        for (String key : cache.keySet()){
            if (key.contains(substring)){
                return Optional.of(cache.get(key));
            }
        }
        return Optional.empty();
    }
}
