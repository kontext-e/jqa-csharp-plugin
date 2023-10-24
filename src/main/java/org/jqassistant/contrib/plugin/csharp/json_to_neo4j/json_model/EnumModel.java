package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class EnumModel extends TypeModel implements JsonModel {

    private List<EnumMemberModel> members;

    @Override
    public String getKey() {
        return super.getFqn();
    }
}
