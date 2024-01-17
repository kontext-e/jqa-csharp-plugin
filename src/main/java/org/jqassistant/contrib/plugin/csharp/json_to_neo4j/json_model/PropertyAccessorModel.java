package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PropertyAccessorModel implements JsonModel{

    private String kind;

    private String accessibility;

    @Override
    public String getKey() {
        return "";
    }
}
