package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertyAnalyzerIT extends CSharpIntegrationTest {


    @Test
    @TestStore(reset = false)
    void testProperties() {
        List<Map<String, Object>> propertyDescriptorList = query("Match (c:Class)-[]->(p:Property) Where c.name=\"Properties\" Return p").getRows();
        assertThat(propertyDescriptorList.size()).isEqualTo(9);
    }

    @Test
    @TestStore(reset = false)
    void testImplicitlyPrivateProperty() {
        Map<String, List<Object>> result = queryForPropertyAndAccessors("ImplicitlyPrivateProperty");

        assertThat(result.get("m").size()).isEqualTo(2);
        assertThat(result.get("p").stream().map(p->(PropertyDescriptor)p).allMatch(p->p.getAccessibility().equals("Private"))).isTrue();
        MethodDescriptor getter = getAccessorOfType(result, "get");
        assertThat(getter.getAccessibility()).isEqualTo("Private");
        MethodDescriptor setter = getAccessorOfType(result, "set");
        assertThat(setter.getAccessibility()).isEqualTo("Private");
    }

    @Test
    @TestStore(reset = false)
    void testPrivateProtectedProperty() {
        PropertyDescriptor property = queryForProperty("PrivateProtectedProperty").get(0);
        assertThat(property.getAccessibility()).isEqualTo("ProtectedAndInternal");
        assertThat(property.getTypes().size()).isEqualTo(1);
        assertThat(property.getTypes().get(0).getFullQualifiedName()).isEqualTo("int");
    }

    @Test
    @TestStore(reset = false)
    void testPropertyWithDifferingAccessorAccessibility(){

        Map<String, List<Object>> result = queryForPropertyAndAccessors("PropertyWithDifferingAccessorAccessibility");

        assertThat(result.get("m").size()).isEqualTo(2);
        MethodDescriptor getter = getAccessorOfType(result, "get");
        assertThat(getter.getAccessibility()).isEqualTo("Public");
        MethodDescriptor setter = getAccessorOfType(result, "set");
        assertThat(setter.getAccessibility()).isEqualTo("Private");
    }

    @Test
    @TestStore(reset = false)
    void testStaticProperty() {
        PropertyDescriptor property = queryForProperty("StaticProperty").get(0);
        assertThat(property.isStatic()).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testExpressionProperty(){
        Map<String, List<Object>> result = queryForPropertyAndAccessors("ExpressionBodiedProperty");

        assertThat(result.get("m").size()).isEqualTo(1);
        MethodDescriptor getter = getAccessorOfType(result, "get");
        assertThat(getter.getName()).startsWith("get");
        assertThat(getter.getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false)
    void testPropertyWithExplicitAccessors(){
        Map<String, List<Object>> result = queryForPropertyAndAccessors("PropertyWithExplicitAccessors");

        assertThat(result.get("m").size()).isEqualTo(2);
        MethodDescriptor getter = getAccessorOfType(result, "get");
        assertThat(getter.getAccessibility()).isEqualTo("Public");
        MethodDescriptor setter = getAccessorOfType(result, "set");
        assertThat(setter.getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false)
    void testPropertyAccessorReturnType(){
        Map<String, List<Object>> result = queryForPropertyAndAccessors("ImplicitlyPrivateProperty");

        assertThat(result.get("m").size()).isEqualTo(2);
        MethodDescriptor getter = getAccessorOfType(result, "get");
        assertThat(getter.getReturns().getFullQualifiedName()).isEqualTo("int");
        MethodDescriptor setter = getAccessorOfType(result, "set");
        assertThat(setter.getReturns().getFullQualifiedName()).isEqualTo("void");
    }

    private static MethodDescriptor getAccessorOfType(Map<String, List<Object>> result, String accessor) {
        return result.get("m").stream()
                .map(m -> (MethodDescriptor) m)
                .filter(m -> m.getName().startsWith(accessor))
                .collect(Collectors.toList())
                .get(0);
    }

    private Map<String, List<Object>> queryForPropertyAndAccessors(String implicitlyPrivateProperty) {
        return query(
                String.format("Match (p:Property)-[:ASSOCIATED_PROPERTY]-(m:Method) where p.name=\"%s\" return p, m",
                        implicitlyPrivateProperty))
                .getColumns();
    }


    private List<PropertyDescriptor> queryForProperty(String nameOfProperty){
        return query(
                String.format("Match (c:Class)-[]->(p:Property) Where c.name=\"%s\" And p.name=\"%s\" Return p",
                        "Properties", nameOfProperty))
                .getColumn("p");
    }

}
