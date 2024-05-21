package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ProjectModel {

    private String name;

    private String absolutePath;

    private String relativePath;

    private List<FileModel> fileModels = new ArrayList<>();

}
