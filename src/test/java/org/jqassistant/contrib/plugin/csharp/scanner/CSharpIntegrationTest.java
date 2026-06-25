package org.jqassistant.contrib.plugin.csharp.scanner;

import com.buschmais.jqassistant.core.test.plugin.AbstractPluginIT;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.JsonToNeo4JConverter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import static org.jqassistant.contrib.plugin.csharp.CSharpToJsonTestRunner.jsonDirectory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CSharpIntegrationTest extends AbstractPluginIT {

    private static boolean scannerHasRun = false;

    @BeforeEach
    public void beforeEachTest(){
        if (!scannerHasRun) {
            store.reset();
            store.beginTransaction();
            scanJsonsToNeo4J();
        } else {
            store.beginTransaction();
        }
    }

    @AfterEach
    public void afterEachTest(){
        store.commitTransaction();
    }

    @Override
    protected boolean isReset() {
        return false;
    }

    private void scanJsonsToNeo4J() {
        JsonToNeo4JConverter jsonToNeo4JConverter = new JsonToNeo4JConverter(store, jsonDirectory);
        jsonToNeo4JConverter.readAllJsonFilesAndSaveToNeo4J();
        scannerHasRun = true;
    }

}
