package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Label(value = "Property")
public interface PropertyDescriptor extends MemberDescriptor, TypedDescriptor{

    @Relation("DECLARES")
    List<MethodDescriptor> getAccessors();

    @Relation.Incoming
    List<MemberAccessDescriptor> getAccessingMethods();

    boolean isRequired();
    void setRequired(boolean required);
}
