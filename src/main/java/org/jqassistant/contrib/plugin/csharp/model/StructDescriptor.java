package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Label("Struct")
public interface StructDescriptor  extends MemberOwningDescriptor {

    @Relation("IMPLEMENTS")
    List<TypeDescriptor> getInterfaces();

}
