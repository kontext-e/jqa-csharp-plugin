package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ClassDescriptorImpl extends MemberOwningTypeDescriptorImpl implements ClassDescriptor {

    public ClassDescriptorImpl(String name) {
        super(name);
    }

    private boolean isAbstract;
    private boolean isStatic;
    private boolean isSealed;

    private String accessibility;

    private TypeDescriptor superClass;
    private List<TypeDescriptor> interfaces = new ArrayList<>();

}
