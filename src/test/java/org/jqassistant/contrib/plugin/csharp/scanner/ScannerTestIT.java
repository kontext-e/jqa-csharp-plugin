package org.jqassistant.contrib.plugin.csharp.scanner;

import com.buschmais.jqassistant.core.scanner.api.DefaultScope;
import com.buschmais.jqassistant.plugin.common.test.AbstractPluginIT;
import org.jqassistant.contrib.plugin.csharp.model.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ScannerTestIT extends AbstractPluginIT {

    private static final String RELATIVE_PATH_TO_TEST_PROJECT = "src/test/resources/scanner/test-csharp-project";

    @Test
    public void test() {

        store.beginTransaction();

        scan();
        testNamespaces();
        testUsings();
        testClasses();
        testBaseTypes();
        testInterfaces();
        testProperties();

        store.commitTransaction();
    }

    private void scan() {

        getScanner().scan(
                new File(RELATIVE_PATH_TO_TEST_PROJECT),
                RELATIVE_PATH_TO_TEST_PROJECT,
                DefaultScope.NONE
        );
    }

    private void testNamespaces() {

        List<NamespaceDescriptor> namespaceDescriptorList = query("MATCH (n:Namespace {fqn: \"Json\"}) RETURN n").getColumn("n");

        assertThat(namespaceDescriptorList).hasSize(1);
        assertThat(namespaceDescriptorList.get(0).getContains().get(0).getFullQualifiedName()).isEqualTo("Json.JsonUtility");
    }

    private void testUsings() {

        List<NamespaceDescriptor> namespaceDescriptorList = query("MATCH (:File {name: \"FileWithUsings.cs\"})-[:USES]->(n:Namespace) RETURN n").getColumn("n");
        assertThat(namespaceDescriptorList).hasSize(3);

        List<UsesNamespaceDescriptor> usesNamespaceDescriptorList = query("MATCH (:File {name: \"FileWithUsings.cs\"})-[u:USES {alias: 'MyAlias'}]->() RETURN u").getColumn("u");
        assertThat(usesNamespaceDescriptorList).hasSize(1);
    }

    private void testClasses() {

        testSimpleClass();
        testAbstractClass();
        testStaticClass();
    }

    private void testSimpleClass() {

        testClass("PublicUtils", false, false);
    }

    private void testAbstractClass() {
        testClass("AbstractUtils", false, true);
    }

    private void testStaticClass() {
        testClass("StaticUtils", true, false);
    }

    private void testClass(String className, boolean expectStatic, boolean expectAbstract) {

        List<ClassDescriptor> classDescriptorList = query("MATCH (c:Class {name: \"" + className + "\"}) RETURN c").getColumn("c");
        assertThat(classDescriptorList).hasSize(1);
        ClassDescriptor classDescriptor = classDescriptorList.get(0);

        if (expectAbstract) {
            assertThat(classDescriptor.isAbstract()).isTrue();
        } else {
            assertThat(classDescriptor.isAbstract()).isFalse();
        }

        if (expectStatic) {
            assertThat(classDescriptor.isStatic()).isTrue();
        } else {
            assertThat(classDescriptor.isStatic()).isFalse();
        }
    }

    private void testBaseTypes() {

        List<ClassDescriptor> classDescriptorList = query("MATCH (c:Class {name: \"Rectangle\"}) RETURN c").getColumn("c");

        assertThat(classDescriptorList).hasSize(1);
        ClassDescriptor classDescriptor = classDescriptorList.get(0);

        assertThat(classDescriptor.getSuperClass()).isNotNull();
        assertThat(classDescriptor.getSuperClass().getName()).isEqualTo("Form");
    }

    private void testInterfaces() {

        List<InterfaceTypeDescriptor> interfaceTypeDescriptorList = query("MATCH (i:Interface {name: \"ChildInterface\"}) RETURN i").getColumn("i");

        assertThat(interfaceTypeDescriptorList).hasSize(1);
        InterfaceTypeDescriptor interfaceTypeDescriptor = interfaceTypeDescriptorList.get(0);

        assertThat(interfaceTypeDescriptor.getInterfaces()).hasSize(1);
        assertThat(interfaceTypeDescriptor.getInterfaces().get(0).getName()).isEqualTo("ParentInterface");
    }

    private void testProperties() {
        List<Map<String, Object>> propertyDescriptorList = query("Match (p:Property) Return p").getRows();
        assertThat(propertyDescriptorList.size()).isEqualTo(12);

        testPropertyInlineGetSetDefinition();
        testPropertyPrivate();
        testPropertyStatic();
        testPropertyInlineGetSetWithVisibility();
        testPropertyExpressionBodiedGetSet();
        testPropertyInit();
        testImplicitAccessModifier();
    }

    private void testPropertyStatic() {
        List<Map<String, Object>> property7 = query("Match (p:Property {name: \"Property8\"}) Return p").getRows();
        assertThat(property7.size()).isEqualTo(1);
        assertThat(((PropertyDescriptor)property7.get(0).get("p")).isStatic()).isEqualTo(true);
    }

    private void testPropertyPrivate() {
        List<Map<String, Object>> property7 = query("Match (p:Property {name: \"Property7\"}) Return p").getRows();
        assertThat(property7.size()).isEqualTo(1);
        assertThat(((PropertyDescriptor)property7.get(0).get("p")).getVisibility()).isEqualTo("Private");
    }

    private void testPropertyInlineGetSetDefinition() {
        List<Map<String, Object>> property1 = query("Match (p:Property {name: \"Property1\"}) Return p").getRows();
        assertThat(property1.size()).isEqualTo(1);

        List<Object> accessors = query("Match r=((p:Property {name: \"Property1\"})-[:DECLARES]->(m)) Return m").getColumn("m");
        assertThat(accessors.size()).isEqualTo(2);
        assertThat(((MethodDescriptor) accessors.get(0)).getVisibility()).isEqualTo("Public");
        assertThat(((MethodDescriptor) accessors.get(1)).getVisibility()).isEqualTo("Public");
    }

    private void testPropertyInlineGetSetWithVisibility() {
        List<Map<String, Object>> property1 = query("Match (p:Property {name: \"Property2\"}) Return p").getRows();
        assertThat(property1.size()).isEqualTo(1);

        List<Object> accessors = query("Match r=((p:Property {name: \"Property2\"})-[:DECLARES]->(m)) Return m").getColumn("m");
        assertThat(accessors.size()).isEqualTo(2);
        for (Object accessor : accessors){
            if (((MethodDescriptor) accessor).getFullQualifiedName().endsWith("set")){
                assertThat(((MethodDescriptor) accessor).getVisibility()).isEqualTo("Private");
            } else {
                assertThat(((MethodDescriptor) accessor).getVisibility()).isEqualTo("Public");
            }
        }
    }

    private void testPropertyExpressionBodiedGetSet() {
        List<Map<String, Object>> property1 = query("Match (p:Property {name: \"Property3\"}) Return p").getRows();
        assertThat(property1.size()).isEqualTo(1);

        List<Object> accessors = query("Match r=((p:Property {name: \"Property3\"})-[:DECLARES]->(m)) Return m").getColumn("m");
        assertThat(accessors.size()).isEqualTo(1);
        assertThat(((MethodDescriptor) accessors.get(0)).getVisibility()).isEqualTo("Public");
    }

    private void testPropertyInit() {
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

    private void testImplicitAccessModifier(){
        List<Map<String, Object>> property10 = query("Match (p:Property {name: \"Property10\"}) Return p").getRows();
        assertThat(property10.size()).isEqualTo(1);
        assertThat(((PropertyDescriptor)property10.get(0).get("p")).getVisibility()).isEqualTo("Private");
    }

}
