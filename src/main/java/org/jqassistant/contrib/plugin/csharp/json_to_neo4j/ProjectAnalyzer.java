package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ProjectModel;
import org.jqassistant.contrib.plugin.csharp.model.ProjectDescriptor;

public class ProjectAnalyzer {

    private final Store store;
    private final CSharpFileCache cSharpFileCache;

    public ProjectAnalyzer(Store store, CSharpFileCache cSharpFileCache){
        this.store = store;
        this.cSharpFileCache = cSharpFileCache;
    }

    protected void createProject(ProjectModel project) {
        ProjectDescriptor projectDescriptor = store.create(ProjectDescriptor.class);
        projectDescriptor.setName(project.getName());
        projectDescriptor.setAbsolutePath(project.getAbsolutePath());
        projectDescriptor.setRelativePath(project.getRelativePath());

        for (FileModel fileModel : project.getFileModels()){
            projectDescriptor.getContainingFiles().add(cSharpFileCache.get(fileModel.getAbsolutePath()));
        }
    }

}
