package org.jqassistant.contrib.plugin.csharp.csharp_to_json;

import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;

import static org.jqassistant.contrib.plugin.csharp.csharp_to_json.CSharpToJsonToolManager.CSHARP_TO_JSON_TOOL_VERSION;

public class CSharpToJsonToolFolders {

    private final AppDirs appDirs;

    public CSharpToJsonToolFolders() {
        appDirs = AppDirsFactory.getInstance();
    }

    public String buildToolPath() {

        return appDirs.getUserDataDir("csharp-to-json-converter", CSHARP_TO_JSON_TOOL_VERSION, "jqassistant-contrib");
    }

    public String buildPluginDataPath() {

        return appDirs.getUserDataDir("csharp-jqassistant-plugin", CSHARP_TO_JSON_TOOL_VERSION, "jqassistant-contrib");
    }
}
