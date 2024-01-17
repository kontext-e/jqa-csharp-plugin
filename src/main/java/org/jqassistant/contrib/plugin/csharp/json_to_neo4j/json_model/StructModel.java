package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StructModel extends TypeModel implements JsonModel{

    private boolean partial;

    private String accessibility;

    private List<String> implementedInterfaces;

    private List<MethodModel> methods;

    private List<ConstructorModel> constructors;

    private List<FieldModel> fields;

    private List<PropertyModel> properties;

    @Override
    public String getKey() {
        return null;
    }
}
