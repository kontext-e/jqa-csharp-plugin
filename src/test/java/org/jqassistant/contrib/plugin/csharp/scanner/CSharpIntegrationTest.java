package org.jqassistant.contrib.plugin.csharp.scanner;

import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.JsonToNeo4JConverter;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;

import static org.jqassistant.contrib.plugin.csharp.CSharpToJsonTestRunner.jsonDirectory;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class CSharpIntegrationTest extends AbstractPluginIT {

    private boolean scannerHasRun = false;

    @BeforeEach
    public void beforeEach(){
        store.beginTransaction();
        if (!scannerHasRun) {
            scanJsonsToNeo4J();
        }
    }

    @AfterEach
    public void afterEach(){
        store.commitTransaction();
    }

    @AfterAll
    public void afterAll(){
        resetStore();
    }

    private void scanJsonsToNeo4J() {
        JsonToNeo4JConverter jsonToNeo4JConverter = new JsonToNeo4JConverter(store, jsonDirectory);
        jsonToNeo4JConverter.readAllJsonFilesAndSaveToNeo4J();
        scannerHasRun = true;
    }

    private void resetStore() {
        store.start();
        store.reset();
        store.stop();
        scannerHasRun = false;
    }
}
