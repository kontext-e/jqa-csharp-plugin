package org.jqassistant.contrib.plugin.csharp;

import org.jqassistant.contrib.plugin.csharp.common.CSharpPluginException;
import org.jqassistant.contrib.plugin.csharp.csharp_to_json.CSharpToJsonToolExecutor;
import org.jqassistant.contrib.plugin.csharp.csharp_to_json.CSharpToJsonToolFolders;
import org.jqassistant.contrib.plugin.csharp.csharp_to_json.CSharpToJsonToolManager;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

import java.io.File;

public class CSharpToJsonTestRunner implements TestExecutionListener {

    public static final String RELATIVE_PATH_TO_TEST_PROJECT = "src/test/resources/scanner/test-csharp-project/CSharpJqAssistantTestProject/CSharpJqAssistantTestProject.sln";
    public static File jsonDirectory;

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan){
        CSharpToJsonToolFolders cSharpToJsonToolFolders = new CSharpToJsonToolFolders();
        CSharpToJsonToolManager cSharpToJsonToolManager = new CSharpToJsonToolManager(cSharpToJsonToolFolders);
        CSharpToJsonToolExecutor cSharpToJsonToolExecutor = new CSharpToJsonToolExecutor(cSharpToJsonToolFolders);

        try {
            cSharpToJsonToolManager.checkIfParserIsAvailableOrDownloadOtherwise();
            jsonDirectory = cSharpToJsonToolExecutor.execute(new File(RELATIVE_PATH_TO_TEST_PROJECT));
        } catch (CSharpPluginException e) {
            throw new RuntimeException(e);
        }
    }

}
