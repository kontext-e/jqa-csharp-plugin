package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class PropertyModel implements JsonModel {

    private String name;

    private String fqn;

    private String type;

    private String accessibility;

    private List<String> accessors;

    @JsonProperty("static")
    private boolean staticKeyword;

    @Override
    public String getKey() {
        return fqn;
    }
}
