package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;

@Getter
@Setter
public class PropertyDescriptorImpl extends FieldDescriptorImpl implements PropertyDescriptor {

    private boolean isRequired;
    private boolean isStatic;
    private boolean isReadOnly;

}
