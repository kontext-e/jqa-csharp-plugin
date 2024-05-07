package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PrimitiveValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class FieldDescriptorImpl implements FieldDescriptor {

    private boolean isStatic;
    private boolean readonly;
    private boolean isConst;
    private boolean isNew;
    private boolean isRequired;
    private boolean isVolatile;

    private String signature;
    private String FullQualifiedName;
    private String name;
    private String accessibility;

    private List<TypeDescriptor> types = new ArrayList<>();
    private PrimitiveValueDescriptor value;
    private MemberOwningTypeDescriptor declaringType;

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
