package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class CSharpFileDescriptorImpl implements CSharpFileDescriptor {

    private String name;
    private String fileName;
    private final Set<FileDescriptor> parents = new LinkedHashSet<>();
    private final List<UsesNamespaceDescriptor> uses = new LinkedList<>();
    private final List<TypeDescriptor> types = new LinkedList<>();

    public CSharpFileDescriptorImpl(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Set<FileDescriptor> getParents() {
        return parents;
    }


    @Override
    public List<UsesNamespaceDescriptor> getUses() {
        return uses;
    }

    @Override
    public List<TypeDescriptor> getTypes() {
        return types;
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
