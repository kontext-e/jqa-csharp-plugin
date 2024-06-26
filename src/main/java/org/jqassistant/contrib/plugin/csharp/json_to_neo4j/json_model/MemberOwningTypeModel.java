package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MemberOwningTypeModel extends TypeModel {

    private List<String> implementedInterfaces;

    private List<MethodModel> methods;

    private List<ConstructorModel> constructors = new ArrayList<>();

    private List<FieldModel> fields;

    private List<PropertyModel> properties;

    public String getKey() {
        return super.getFqn();
    }
}
