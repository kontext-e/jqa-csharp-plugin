package org.jqassistant.contrib.plugin.csharp.scanner;

import com.buschmais.jqassistant.plugin.common.api.model.NamedDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertyAnalyzerIT extends CSharpIntegrationTest {


    @Test
    @TestStore(reset = false)
    void testProperties() {
        List<Map<String, Object>> propertyDescriptorList = query("Match (c:Class)-[]->(p:Property) Where c.name=\"Properties\" Return p").getRows();
        assertThat(propertyDescriptorList.size()).isEqualTo(7);
    }

    @Test
    @TestStore(reset = false)
    void testImplicitlyPrivateProperty() {
        PropertyDescriptor property = queryForProperty("ImplicitlyPrivateProperty").get(0);

        assertThat(property.getAccessibility()).isEqualTo("Private");
        assertThat(property.getAccessors().get(0).getAccessibility()).isEqualTo("Private");
        List<MethodDescriptor> accessors = new LinkedList<>(property.getAccessors());
        accessors.sort(comparing(NamedDescriptor::getName));
        assertThat(accessors.get(0).getName()).startsWith("get");
        assertThat(accessors.get(1).getName()).startsWith("init");
    }

    @Test
    @TestStore(reset = false)
    void testPrivateProtectedProperty() {
        PropertyDescriptor property = queryForProperty("PrivateProtectedProperty").get(0);
        assertThat(property.getAccessibility()).isEqualTo("ProtectedAndInternal");
        assertThat(property.getType().getFullQualifiedName()).isEqualTo("int");
    }

    @Test
    @TestStore(reset = false)
    void testPropertyWithDifferingAccessorAccessibility(){
        PropertyDescriptor property = queryForProperty("PropertyWithDifferingAccessorAccessibility").get(0);

        assertThat(property.getAccessors().size()).isEqualTo(2);
        List<MethodDescriptor> accessors = new LinkedList<>(property.getAccessors());
        accessors.sort(comparing(NamedDescriptor::getName));
        assertThat(accessors.get(0).getName()).startsWith("get");
        assertThat(accessors.get(0).getAccessibility()).isEqualTo("Public");
        assertThat(accessors.get(1).getName()).startsWith("set");
        assertThat(accessors.get(1).getAccessibility()).isEqualTo("Private");
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
        PropertyDescriptor property = queryForProperty("ExpressionBodiedProperty").get(0);

        assertThat(property.getAccessors().size()).isEqualTo(1);
        assertThat(property.getAccessors().get(0).getName()).startsWith("get");
        assertThat(property.getAccessors().get(0).getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false)
    void testPropertyWithExplicitAccessors(){
        PropertyDescriptor property = queryForProperty("PropertyWithExplicitAccessors").get(0);

        assertThat(property.getAccessors().size()).isEqualTo(2);
        List<MethodDescriptor> accessors = new LinkedList<>(property.getAccessors());
        accessors.sort(comparing(NamedDescriptor::getName));
        assertThat(accessors.get(0).getName()).startsWith("get");
        assertThat(accessors.get(0).getAccessibility()).isEqualTo("Public");
        assertThat(accessors.get(1).getName()).startsWith("set");
        assertThat(accessors.get(1).getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false)
    void testPropertyAccessorReturnType(){
        PropertyDescriptor property = queryForProperty("ImplicitlyPrivateProperty").get(0);

        MethodDescriptor init = property.getAccessors().stream().filter(a -> a.getName().contains("init")).findAny().get();
        assertThat(init.getReturns().getFullQualifiedName()).isEqualTo("void");
        MethodDescriptor get = property.getAccessors().stream().filter(a -> a.getName().contains("get")).findAny().get();
        assertThat(get.getReturns().getFullQualifiedName()).isEqualTo("int");
    }


    private List<PropertyDescriptor> queryForProperty(String nameOfProperty){
        return query(
                String.format("Match (c:Class)-[]->(p:Property) Where c.name=\"%s\" And p.name=\"%s\" Return p",
                        "Properties", nameOfProperty))
                .getColumn("p");
    }

}
