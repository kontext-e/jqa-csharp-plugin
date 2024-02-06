package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

@Getter
@Setter
public class ParameterDescriptorImpl implements ParameterDescriptor {

    private int index;
    private String name;
    private TypeDescriptor type;

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
