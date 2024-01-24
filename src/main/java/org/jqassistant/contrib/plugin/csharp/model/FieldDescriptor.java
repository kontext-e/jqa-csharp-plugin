package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Property;
import com.buschmais.xo.neo4j.api.annotation.Relation;

@SuppressWarnings("unused")
@Label(value = "Field")
public interface FieldDescriptor extends MemberDescriptor, TypedDescriptor {

    @Property("volatile")
    boolean isVolatile();
    void setVolatile(boolean volatileField);

    @Relation("HAS")
    PrimitiveValueDescriptor getValue();
    void setValue(PrimitiveValueDescriptor valueDescriptor);

    boolean isRequired();
    void setRequired(boolean required);

    boolean isStatic();
    void setStatic(boolean s);
}
