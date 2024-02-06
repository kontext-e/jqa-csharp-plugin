package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ClassModel extends TypeModel implements JsonModel {


    @JsonProperty("abstract")
    private boolean abstractKeyword;

    private boolean sealed;

    @JsonProperty("static")
    private boolean staticKeyword;

    private String accessibility;

    private String baseType;

    private List<String> implementedInterfaces;

    private List<MethodModel> methods;

    private List<ConstructorModel> constructors;

    private List<FieldModel> fields;

    private List<PropertyModel> properties;

    public String getKey() {
        return super.getFqn();
    }
}
