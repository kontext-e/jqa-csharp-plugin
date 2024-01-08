package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.EnumValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

public class EnumValueDescriptorIml implements EnumValueDescriptor {

    private String name;
    private TypeDescriptor type;
    private FieldDescriptor value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public TypeDescriptor getType() {
        return type;
    }

    @Override
    public void setType(TypeDescriptor type) {
        this.type = type;
    }

    public FieldDescriptor getValue() {
        return value;
    }

    public void setValue(FieldDescriptor value) {
        this.value = value;
    }

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
