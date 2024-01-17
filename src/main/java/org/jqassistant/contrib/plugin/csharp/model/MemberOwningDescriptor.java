package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

@Label("MemberOwning")
public interface MemberOwningDescriptor extends TypeDescriptor{

    @Relation.Outgoing
    @Declares
    List<MemberDescriptor> getDeclaredMembers();

}
