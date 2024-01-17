package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyAnalyzerIT extends CSharpIntegrationTest {


    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testProperties() {
        List<Map<String, Object>> propertyDescriptorList = query("Match (p:Property) Return p").getRows();

        assertThat(propertyDescriptorList.size()).isEqualTo(12);
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testPropertyStatic() {
        List<Map<String, Object>> property7 = query("Match (p:Property {name: \"Property8\"}) Return p").getRows();

        assertThat(property7.size()).isEqualTo(1);
        assertThat(((PropertyDescriptor)property7.get(0).get("p")).isStatic()).isEqualTo(true);
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testPropertyPrivate() {
        List<Map<String, Object>> property7 = query("Match (p:Property {name: \"Property7\"}) Return p").getRows();

        assertThat(property7.size()).isEqualTo(1);
        assertThat(((PropertyDescriptor)property7.get(0).get("p")).getAccessibility()).isEqualTo("Private");
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testPropertyInlineGetSetDefinition() {
        List<Map<String, Object>> property1 = query("Match (p:Property {name: \"Property1\"}) Return p").getRows();
        assertThat(property1.size()).isEqualTo(1);

        List<Object> accessors = query("Match r=((p:Property {name: \"Property1\"})-[:DECLARES]->(m)) Return m").getColumn("m");
        assertThat(accessors.size()).isEqualTo(2);
        assertThat(((MethodDescriptor) accessors.get(0)).getAccessibility()).isEqualTo("Public");
        assertThat(((MethodDescriptor) accessors.get(1)).getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testPropertyInlineGetSetWithVisibility() {
        List<Map<String, Object>> property1 = query("Match (p:Property {name: \"Property2\"}) Return p").getRows();
        assertThat(property1.size()).isEqualTo(1);

        List<Object> accessors = query("Match r=((p:Property {name: \"Property2\"})-[:DECLARES]->(m)) Return m").getColumn("m");
        assertThat(accessors.size()).isEqualTo(2);
        for (Object accessor : accessors){
            if (((MethodDescriptor) accessor).getFullQualifiedName().endsWith("set")){
                assertThat(((MethodDescriptor) accessor).getAccessibility()).isEqualTo("Private");
            } else {
                assertThat(((MethodDescriptor) accessor).getAccessibility()).isEqualTo("Public");
            }
        }
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testPropertyExpressionBodiedGetSet() {
        List<Map<String, Object>> property1 = query("Match (p:Property {name: \"Property3\"}) Return p").getRows();
        assertThat(property1.size()).isEqualTo(1);

        List<Object> accessors = query("Match r=((p:Property {name: \"Property3\"})-[:DECLARES]->(m)) Return m").getColumn("m");
        assertThat(accessors.size()).isEqualTo(1);
        assertThat(((MethodDescriptor) accessors.get(0)).getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testPropertyInit() {
        boolean initDetected = false;
        List<Object> accessors = query("Match r=((p:Property {name: \"Property6\"})-[:DECLARES]->(m)) Return m").getColumn("m");
        assertThat(accessors.size()).isEqualTo(2);
        for (Object accessor : accessors){
            if (((MethodDescriptor)accessor).getFullQualifiedName().endsWith("init")){
                initDetected = true;
                break;
            }
        }
        assertThat(initDetected).isTrue();
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testImplicitAccessModifier(){
        List<Map<String, Object>> property10 = query("Match (p:Property {name: \"Property10\"}) Return p").getRows();

        assertThat(property10.size()).isEqualTo(1);
        assertThat(((PropertyDescriptor)property10.get(0).get("p")).getAccessibility()).isEqualTo("Private");
    }

}
