package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.jqassistant.core.store.api.model.Descriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;

import static com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;


@Label("Invocation")
public interface InvokesDescriptor extends Descriptor, LineNumberDescriptor {

    @Outgoing
    @Relation("INVOKES")
    MethodDescriptor getInvokedMethod();

    void setInvokedMethod(MethodDescriptor methodDescriptor);

    @Relation("WITH_TYPE_ARGUMENT")
    List<TypeDescriptor> getGenericTypeArguments();
}
