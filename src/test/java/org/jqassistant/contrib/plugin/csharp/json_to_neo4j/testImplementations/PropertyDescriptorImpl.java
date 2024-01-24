package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.MemberAccessDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;

import java.util.ArrayList;
import java.util.List;

public class PropertyDescriptorImpl extends FieldDescriptorImpl implements PropertyDescriptor {

    private List<MethodDescriptor> accessors = new ArrayList<>();
    private List<MemberAccessDescriptor> accessingMethods = new ArrayList<>();
    private boolean isRequired;

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public void setRequired(boolean required) {
        isRequired = required;
    }

    @Override
    public List<MethodDescriptor> getAccessors() {
        return accessors;
    }

    @Override
    public List<MemberAccessDescriptor> getAccessingMethods() {
        return accessingMethods;
    }
}
