package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MemberOwningTypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.PropertyModel;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

public class PropertyAnalyzer {

    private final TypeCache typeCache;
    private final PropertyCache propertyCache;

    public PropertyAnalyzer(TypeCache typeCache, PropertyCache propertyCache) {
        this.typeCache = typeCache;
        this.propertyCache = propertyCache;
    }

    public void createProperties(MemberOwningTypeModel memberOwningTypeModel) {
        MemberOwningTypeDescriptor typeDescriptor = (MemberOwningTypeDescriptor) typeCache.findAny(memberOwningTypeModel.getKey());

        for (PropertyModel propertyModel : memberOwningTypeModel.getProperties()) {
            PropertyDescriptor propertyDescriptor = propertyCache.create(propertyModel.getKey());
            fillPropertyDescriptor(propertyModel, propertyDescriptor);
            typeDescriptor.getDeclaredMembers().add(propertyDescriptor);
        }
    }

    private void fillPropertyDescriptor(PropertyModel propertyModel, PropertyDescriptor propertyDescriptor) {
        propertyDescriptor.setFullQualifiedName(propertyModel.getFqn());
        propertyDescriptor.setName(propertyModel.getName());
        propertyDescriptor.setAccessibility(propertyModel.getAccessibility());
        propertyDescriptor.setStatic(propertyModel.isStaticKeyword());
        propertyDescriptor.setRequired(propertyModel.isRequired());
        propertyDescriptor.setReadOnly(propertyModel.isReadonly());

        TypeDescriptor typeDescriptor = typeCache.findOrCreate(propertyModel.getType());
        propertyDescriptor.setType(typeDescriptor);

    }
}
