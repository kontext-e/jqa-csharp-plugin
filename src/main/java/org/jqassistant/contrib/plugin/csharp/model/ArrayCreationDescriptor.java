package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

@Relation("CREATES_ARRAY")
public interface ArrayCreationDescriptor extends Descriptor, LineNumberDescriptor {

    @Outgoing
    MethodDescriptor getCreatingMethod();

    @Incoming
    TypeDescriptor getCreatedType();
}
