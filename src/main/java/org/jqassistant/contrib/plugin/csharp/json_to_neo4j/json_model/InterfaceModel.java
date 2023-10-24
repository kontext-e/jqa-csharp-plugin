package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class InterfaceModel extends TypeModel implements JsonModel {

    private String accessibility;

    private String md5;

    private List<MethodModel> methods;

    private List<String> implementedInterfaces;

    @Override
    public String getKey() {
        return super.getFqn();
    }
}
