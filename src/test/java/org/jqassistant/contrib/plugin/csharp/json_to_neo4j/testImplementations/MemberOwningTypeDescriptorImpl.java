package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningTypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class MemberOwningTypeDescriptorImpl extends TypeDescriptorImpl implements MemberOwningTypeDescriptor {

    private List<MemberDescriptor> declaredMembers = new ArrayList<>();

    public MemberOwningTypeDescriptorImpl(String name) {
        super(name);
    }

    @Override
    public List<MemberDescriptor> getDeclaredMembers() {
        return declaredMembers;
    }

}
