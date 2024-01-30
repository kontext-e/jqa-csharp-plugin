package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.jqassistant.core.store.api.model.FullQualifiedNameDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.MD5Descriptor;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;


@Label(value = "Type", usingIndexedPropertyOf = FullQualifiedNameDescriptor.class)
public interface TypeDescriptor extends
        CSharpDescriptor,
        NamedDescriptor,
        FullQualifiedNameDescriptor,
        MD5Descriptor,
        PartialDescriptor,
        AccessibilityDescriptor,
        LineCountDescriptor {



    @Relation("PARTIAL_WITH")
    List<TypeDescriptor> getTypeFragments();

}
