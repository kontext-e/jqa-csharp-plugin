package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;


public interface TypedDescriptor {

    @Relation("OF_TYPE")
    List<TypeDescriptor> getTypes();
    void setTypes(List<TypeDescriptor> type);
}
