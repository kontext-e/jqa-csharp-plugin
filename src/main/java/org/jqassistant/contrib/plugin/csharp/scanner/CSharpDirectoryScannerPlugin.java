package org.jqassistant.contrib.plugin.csharp.scanner;

import com.buschmais.jqassistant.core.scanner.api.Scanner;
import com.buschmais.jqassistant.core.scanner.api.Scope;
import com.buschmais.jqassistant.plugin.common.api.scanner.AbstractScannerPlugin;
import com.buschmais.jqassistant.plugin.common.api.scanner.filesystem.FileResource;
import org.apache.commons.io.FileUtils;
import org.jqassistant.contrib.plugin.csharp.common.CSharpPluginException;
import org.jqassistant.contrib.plugin.csharp.csharp_to_json.CSharpToJsonToolExecutor;
import org.jqassistant.contrib.plugin.csharp.csharp_to_json.CSharpToJsonToolFolders;
import org.jqassistant.contrib.plugin.csharp.csharp_to_json.CSharpToJsonToolManager;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.JsonToNeo4JConverter;
import org.jqassistant.contrib.plugin.csharp.model.CSharpDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;


public class CSharpDirectoryScannerPlugin extends AbstractScannerPlugin<FileResource, CSharpDescriptor> {

    private static final boolean DEBUG = true;
    private static final Logger LOGGER = LoggerFactory.getLogger(CSharpDirectoryScannerPlugin.class);

    private final CSharpToJsonToolManager cSharpToJsonToolManager;
    private final CSharpToJsonToolExecutor cSharpToJsonToolExecutor;

    private File jsonDirectory;

    public CSharpDirectoryScannerPlugin() {

        CSharpToJsonToolFolders cSharpToJsonToolFolders = new CSharpToJsonToolFolders();
        cSharpToJsonToolManager = new CSharpToJsonToolManager(cSharpToJsonToolFolders);
        cSharpToJsonToolExecutor = new CSharpToJsonToolExecutor(cSharpToJsonToolFolders);
    }

    @Override
    public boolean accepts(FileResource item, String path, Scope scope) throws IOException {
        return item.getFile().getName().endsWith(".sln");
    }

    @Override
    public CSharpDescriptor scan(FileResource file, String path, Scope scope, Scanner scanner) throws IOException {

        try {
            cSharpToJsonToolManager.checkIfParserIsAvailableOrDownloadOtherwise();
            jsonDirectory = cSharpToJsonToolExecutor.execute(file.getFile());

            JsonToNeo4JConverter jsonToNeo4JConverter = new JsonToNeo4JConverter(scanner.getContext().getStore(), jsonDirectory);
            jsonToNeo4JConverter.readAllJsonFilesAndSaveToNeo4J();

        } catch (CSharpPluginException e) {
            e.printStackTrace();
        }

        LOGGER.info("Deleting JSON folder at '{}' ...", jsonDirectory.getAbsolutePath());
        FileUtils.deleteDirectory(jsonDirectory.toPath().toFile());
        return null;
    }
}
