package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InterfaceModel extends MemberOwningTypeModel implements JsonModel {

    private String accessibility;

    private boolean partial;

    private String md5;

    @Override
    public String getKey() {
        return super.getFqn();
    }
}
