package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ClassModel extends MemberOwningTypeModel implements JsonModel {


    @JsonProperty("abstract")
    private boolean abstractKeyword;

    private boolean sealed;

    private boolean partial;

    @JsonProperty("static")
    private boolean staticKeyword;

    private String accessibility;

    private String baseType;


    public String getKey() {
        return super.getFqn();
    }
}
