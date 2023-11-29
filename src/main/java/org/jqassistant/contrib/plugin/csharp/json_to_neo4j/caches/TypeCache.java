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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TypeCache {

    private final Store store;
    private final HashMap<String, List<TypeDescriptor>> cache;

    public TypeCache(Store store) {
        this.store = store;
        this.cache = new HashMap<>();
    }

    public TypeDescriptor findOrCreate(String type) {
        if (cache.containsKey(type)) {
            return findAny(type);
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
        descriptor.setFullQualifiedName(fqn);

        if (cache.containsKey(fqn)){
            cache.get(fqn).add(descriptor);
        } else {
            List<TypeDescriptor> newList = new ArrayList<>();
            newList.add(descriptor);
            cache.put(fqn, newList);
        }

        return descriptor;
    }

    public TypeDescriptor findAny(String key){
        List<TypeDescriptor> typeDescriptors = cache.get(key);
        return typeDescriptors.get(0);
    }

    public List<TypeDescriptor> findAll(String key) {
        return cache.get(key);
    }

    public Optional<TypeDescriptor> findTypeByRelativePath(String key, String path){
        List<TypeDescriptor> typeDescriptors = findAll(key);
        return typeDescriptors.stream().filter(item -> item.getRelativePath().equals(path)).findAny();
    }
}
