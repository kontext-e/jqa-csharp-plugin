package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@SuppressWarnings("unused")
@Label(value = "Property")
public interface PropertyDescriptor extends MemberDescriptor, TypedDescriptor{

    @Relation("DECLARES")
    List<MethodDescriptor> getAccessors();
    void setAccessors(List<MethodDescriptor> accessors);

    @Relation.Incoming
    List<MemberAccessDescriptor> getAccessingMethods();
    void setAccessingMethods(List<MemberAccessDescriptor> accessingMethods);

    boolean isRequired();
    void setRequired(boolean required);

    boolean isStatic();
    void setStatic(boolean s);
}
