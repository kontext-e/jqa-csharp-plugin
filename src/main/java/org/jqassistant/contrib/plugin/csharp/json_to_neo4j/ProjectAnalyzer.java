package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ProjectModel;
import org.jqassistant.contrib.plugin.csharp.model.ProjectDescriptor;

import java.util.List;

public class ProjectAnalyzer {

    private final Store store;

    public ProjectAnalyzer(Store store){

        this.store = store;
    }

    protected void createProjects(List<ProjectModel> projects){
        for (ProjectModel project : projects){
           createProject(project);
        }
    }

    private void createProject(ProjectModel project) {
        ProjectDescriptor projectDescriptor = store.create(ProjectDescriptor.class);
        projectDescriptor.setName(project.getName());
        projectDescriptor.setAbsolutePath(project.getAbsolutePath());
        projectDescriptor.setRelativePath(project.getRelativePath());
    }

}
