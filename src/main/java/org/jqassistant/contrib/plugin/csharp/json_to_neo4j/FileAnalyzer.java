package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpDirectoryCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.model.CSharpClassesDirectoryDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;

public class FileAnalyzer {

    private final CSharpFileCache cSharpFileCache;
    private final CSharpDirectoryCache cSharpDirectoryCache;

    public FileAnalyzer(CSharpFileCache cSharpFileCache, CSharpDirectoryCache cSharpDirectoryCache) {
        this.cSharpFileCache = cSharpFileCache;
        this.cSharpDirectoryCache = cSharpDirectoryCache;
    }

    public void analyzeFile(FileModel fileModel){
        CSharpFileDescriptor cSharpFileDescriptor = cSharpFileCache.create(fileModel.getAbsolutePath());
        cSharpFileDescriptor.setName(fileModel.getName());
        cSharpFileDescriptor.setFileName(fileModel.getRelativePath());

        createMissingFolderStructure(getParentDirectoryOf(fileModel.getRelativePath()), cSharpFileDescriptor);
    }

    private void createMissingFolderStructure(String relativePath, FileDescriptor containingFile) {
        //If Descriptor is existent, directory structure already exists
        CSharpClassesDirectoryDescriptor parentDescriptor = cSharpDirectoryCache.get(relativePath);
        if (parentDescriptor != null){
            parentDescriptor.getContains().add(containingFile);
            return;
        }

        CSharpClassesDirectoryDescriptor cSharpFileDescriptor = createDirectoryDescriptor(relativePath, containingFile);
        createMissingFolderStructure(getParentDirectoryOf(relativePath), cSharpFileDescriptor);
    }

    private CSharpClassesDirectoryDescriptor createDirectoryDescriptor(String relativePath, FileDescriptor containingFile) {
        CSharpClassesDirectoryDescriptor cSharpFileDescriptor = cSharpDirectoryCache.create(relativePath);
        cSharpFileDescriptor.setFileName(relativePath);
        cSharpFileDescriptor.getContains().add(containingFile);
        return cSharpFileDescriptor;
    }

    private String getParentDirectoryOf(String filePath){
        if (!filePath.contains("\\")) return "\\";
        return filePath.substring(0, filePath.lastIndexOf("\\"));
    }
}
