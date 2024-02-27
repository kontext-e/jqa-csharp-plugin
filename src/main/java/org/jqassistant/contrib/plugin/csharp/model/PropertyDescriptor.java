package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Label;

@SuppressWarnings("unused")
@Label(value = "Property")
public interface PropertyDescriptor extends MemberDescriptor, TypedDescriptor{

    boolean isReadOnly();
    void setReadOnly(boolean readOnly);

    boolean isRequired();
    void setRequired(boolean required);

    boolean isStatic();
    void setStatic(boolean s);
}
