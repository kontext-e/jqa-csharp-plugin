package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.ArrayCreationDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class TypeDescriptorImpl implements TypeDescriptor {

    public TypeDescriptorImpl(String name){
        this.name = name;
    }

    private List<TypeDescriptor> typeFragments = new LinkedList<>();
    private List<ArrayCreationDescriptor> creatingMethods;
    private String name;
    private Integer lastLineNumber;
    private Integer firstLineNumber;
    private Integer effectiveLineCount;
    private String fullQualifiedName;
    private String Md5;
    private String relativePath;
    private boolean partial;
    private String accessibility;

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

