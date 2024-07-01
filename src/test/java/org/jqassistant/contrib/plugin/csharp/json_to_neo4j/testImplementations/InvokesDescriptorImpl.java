package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.List;

@Getter
@Setter
public class InvokesDescriptorImpl implements InvokesDescriptor {

    private int lineNumber;

    private MethodDescriptor invokedMethod;
    private MethodDescriptor invokingMethod;
    private List<TypeDescriptor> genericTypeArguments;

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
