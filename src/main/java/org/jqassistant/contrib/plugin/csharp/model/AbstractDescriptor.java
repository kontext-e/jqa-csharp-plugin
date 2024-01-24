package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Property;

public interface AbstractDescriptor {

    @Property("abstract")
    boolean isAbstract();

    void setAbstract(boolean isAbstract);

}
