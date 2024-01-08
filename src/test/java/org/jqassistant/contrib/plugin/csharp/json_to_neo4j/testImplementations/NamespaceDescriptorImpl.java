package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;

import java.util.LinkedList;
import java.util.List;

public class NamespaceDescriptorImpl implements NamespaceDescriptor {

    private String fullQualifiedName;
    private List<UsesNamespaceDescriptor> usedBy = new LinkedList<>();
    private List<TypeDescriptor> contains = new LinkedList<>();

    public NamespaceDescriptorImpl() {
    }

    @Override
    public List<UsesNamespaceDescriptor> getUsedBy() {
        return usedBy;
    }

    @Override
    public List<TypeDescriptor> getContains() {
        return contains;
    }

    @Override
    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    @Override
    public void setFullQualifiedName(String s) {
        fullQualifiedName = s;
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
