package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;


@Relation("CALLS")
public interface CallDescriptor extends Descriptor, LineNumberDescriptor {

    @Outgoing
    MethodDescriptor getCallingMethod();

    @Incoming
    MemberDescriptor getCalledMember();
}
