package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MemberOwningModel extends TypeModel {

    private String accessibility;

    private List<String> implementedInterfaces;

    private List<MethodModel> methods;

    private List<ConstructorModel> constructors;

    private List<FieldModel> fields;

    private List<PropertyModel> properties;

}
