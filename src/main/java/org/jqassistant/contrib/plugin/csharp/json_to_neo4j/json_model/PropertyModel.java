package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PropertyModel implements JsonModel {

    private String name;

    private String fqn;

    private List<String> types = new LinkedList<>();

    private String accessibility;

    @JsonProperty("static")
    private boolean staticKeyword;

    private boolean required;

    private boolean readonly;

    @Override
    public String getKey() {
        return fqn;
    }
}
