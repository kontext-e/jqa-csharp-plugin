package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.MemberAccessDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PropertyDescriptorImpl extends FieldDescriptorImpl implements PropertyDescriptor {

    private boolean isRequired;
    private boolean isStatic;

    private List<MethodDescriptor> accessors = new ArrayList<>();
    private List<MemberAccessDescriptor> accessingMethods = new ArrayList<>();

}
