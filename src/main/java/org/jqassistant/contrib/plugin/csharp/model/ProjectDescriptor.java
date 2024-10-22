package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;

import java.util.List;


@Label("Project")
public interface ProjectDescriptor extends NamedDescriptor, CSharpDescriptor {

    String getRelativePath();
    void setRelativePath(String relativePath);

    String getAbsolutePath();
    void setAbsolutePath(String absolutePath);

    @Relation("CONTAINS")
    List<FileDescriptor> getContainingFiles();

}
