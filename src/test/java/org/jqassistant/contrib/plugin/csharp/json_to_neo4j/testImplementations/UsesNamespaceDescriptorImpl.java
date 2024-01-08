package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;

public class UsesNamespaceDescriptorImpl implements UsesNamespaceDescriptor {

    private String alias;
    private CSharpFileDescriptor usingCSharpFile;
    private NamespaceDescriptor usedNamespace;

    @Override
    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    @Override
    public CSharpFileDescriptor getUsingCSharpFile() {
        return usingCSharpFile;
    }

    @Override
    public NamespaceDescriptor getUsedNamespace() {
        return usedNamespace;
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
