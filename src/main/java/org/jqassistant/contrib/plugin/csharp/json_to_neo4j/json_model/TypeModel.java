package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TypeModel {

    private String name;

    private String fqn;

    private String md5;

    private int firstLineNumber;

    private int lastLineNumber;

    private int effectiveLineCount;
}
