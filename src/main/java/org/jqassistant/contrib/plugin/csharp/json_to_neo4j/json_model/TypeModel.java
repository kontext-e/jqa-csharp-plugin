package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TypeModel {

    private String accessibility;

    private String relativePath;

    private String name;

    private String fqn;

    private int firstLineNumber;

    private int lastLineNumber;

    private int effectiveLineCount;
}
