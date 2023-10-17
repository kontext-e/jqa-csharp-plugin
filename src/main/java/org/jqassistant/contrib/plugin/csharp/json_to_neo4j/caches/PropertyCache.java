package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;

import java.util.HashMap;

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
}
