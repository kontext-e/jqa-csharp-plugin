package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.EnumModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.TypeModel;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.HashMap;

public class TypeCache {

    private final Store store;
    private final HashMap<String, TypeDescriptor> cache;

    public TypeCache(Store store) {
        this.store = store;
        this.cache = new HashMap<>();
    }

    public TypeDescriptor findOrCreate(String type) {
        if (cache.containsKey(type)) {
            return find(type);
        }
        //Using TypeDescriptor as from name alone it
        //is not clear what kind of type it is
        return create(type, TypeDescriptor.class);
    }

    public TypeDescriptor create(TypeModel typeModel){
        if (typeModel instanceof  ClassModel){
            return create (typeModel.getFqn(), ClassDescriptor.class);

        } else if (typeModel instanceof EnumModel) {
            return create(typeModel.getFqn(), EnumTypeDescriptor.class);

        } else if (typeModel instanceof  InterfaceModel) {
            return create(typeModel.getFqn(), InterfaceTypeDescriptor.class);

        } else {
            return null;
        }
    }

    protected <D extends TypeDescriptor> D create(String fqn, Class<D> descriptorClass) {
        D descriptor = store.create(descriptorClass);
        cache.put(fqn, descriptor);
        descriptor.setFullQualifiedName(fqn);
        return descriptor;
    }

    public TypeDescriptor find(String key) {
        return cache.get(key);
    }
}
