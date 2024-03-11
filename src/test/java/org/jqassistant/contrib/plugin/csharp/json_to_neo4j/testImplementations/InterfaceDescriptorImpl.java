package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class InterfaceDescriptorImpl extends MemberOwningTypeDescriptorImpl implements InterfaceTypeDescriptor {

    private final List<TypeDescriptor> interfaces = new ArrayList<>();

    public InterfaceDescriptorImpl(String name) {
        super(name);
    }

    @Override
    public List<TypeDescriptor> getInterfaces() {
        return interfaces;
    }
}
