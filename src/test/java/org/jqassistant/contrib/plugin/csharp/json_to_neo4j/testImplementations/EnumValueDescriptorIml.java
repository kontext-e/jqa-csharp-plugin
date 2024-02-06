package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.EnumValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

@Getter
@Setter
public class EnumValueDescriptorIml implements EnumValueDescriptor {

    private String name;
    private TypeDescriptor type;
    private FieldDescriptor value;

    @Override
    public <I> I getId() {
        return null;
    }

    @Override
    public <T> T as(Class<T> aClass) {
        return null;
    }

    @Override
    public <D> D getDelegate() {
        return null;
    }
}
