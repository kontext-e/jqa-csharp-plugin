package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StructModel extends MemberOwningTypeModel implements JsonModel{

    private boolean partial;

}
