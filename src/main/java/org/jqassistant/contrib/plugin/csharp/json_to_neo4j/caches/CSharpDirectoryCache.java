package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.model.CSharpClassesDirectoryDescriptor;

import java.util.HashMap;

public class CSharpDirectoryCache {

    private final Store store;
    private final HashMap<String, CSharpClassesDirectoryDescriptor> cache;

    public CSharpDirectoryCache(Store store) {
        this.store = store;
        this.cache = new HashMap<>();
    }

    public CSharpClassesDirectoryDescriptor findOrCreate(String absolutePath) {

        if (cache.containsKey(absolutePath)) {
            return cache.get(absolutePath);
        }

        return create(absolutePath);
    }

    public CSharpClassesDirectoryDescriptor create(String absolutePath) {

        CSharpClassesDirectoryDescriptor descriptor = store.create(CSharpClassesDirectoryDescriptor.class);
        cache.put(absolutePath, descriptor);
        return descriptor;
    }

    public CSharpClassesDirectoryDescriptor get(String absolutePath) {
        return cache.get(absolutePath);
    }
}
