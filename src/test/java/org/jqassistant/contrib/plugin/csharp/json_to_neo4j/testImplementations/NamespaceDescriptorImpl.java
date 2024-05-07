package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class NamespaceDescriptorImpl implements NamespaceDescriptor {

    private String fullQualifiedName;
    private List<UsesNamespaceDescriptor> usedBy = new LinkedList<>();
    private List<TypeDescriptor> contains = new LinkedList<>();
    private List<NamespaceDescriptor> containingNameSpaces = new LinkedList<>();

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
