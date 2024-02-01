package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Property;


public interface LineNumberDescriptor {

    @Property("lineNumber")
    int getLineNumber();

    void setLineNumber(int lineNumber);

}
