package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InvocationAnalyzerIT extends CSharpIntegrationTest {

    @Test
    @TestStore(reset = false)
    void testConstructorInvocations(){
        List<MethodDescriptor> invokedMethods = query("Match (m:Method)-[:INVOKES]-(n:Constructor) Where m.name=\"Invocations\" Return n").getColumn("n");
        assertThat(invokedMethods.size()).isEqualTo(2);
        assertThat(invokedMethods.stream().anyMatch(i -> i.getInvokedBy().stream().anyMatch(m-> m.getInvokingMethod().getName().equals("TypeClass")))).isTrue();
        assertThat(invokedMethods.stream().anyMatch(i -> i.getInvokedBy().stream().anyMatch(m-> m.getInvokingMethod().getName().equals("TypeStruct")))).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testMethodInvocations(){
        List<MethodDescriptor> invokedMethods = query("Match (m:Method)-[:INVOKES]-(n:Method) Where m.name=\"Invocations\" Return n").getColumn("n");

        List<MethodDescriptor> methods = invokedMethods.stream().filter(m -> !(m instanceof ConstructorDescriptor)).collect(Collectors.toList());
        assertThat(methods.size()).isEqualTo(5);
        assertThat(methods.stream().anyMatch(m -> m.getFullQualifiedName().contains("Method()"))).isTrue();
        assertThat(methods.stream().anyMatch(m -> m.getFullQualifiedName().contains("WriteLine(string?)"))).isTrue();
        assertThat(methods.stream().anyMatch(m -> m.getFullQualifiedName().contains("PartialMethod()"))).isTrue();
        assertThat(methods.stream().anyMatch(m -> m.getFullQualifiedName().contains("PartialClass()"))).isTrue();
        assertThat(methods.stream().anyMatch(m -> m.getFullQualifiedName().contains("ExtensionMethodWithArgument"))).isTrue();
    }


    @Test
    @TestStore(reset = false)
    void testPropertyAccesses(){
        List<PropertyDescriptor> properties = query("Match (m:Method)-[:ACCESSES]-(p:Property) where m.name=\"Invocations\" Return p").getColumn("p");
        assertThat(properties.size()).isEqualTo(4);
        assertThat(properties.stream().anyMatch(p -> p.getFullQualifiedName().contains("TypeStruct.Property"))).isTrue();
        assertThat(properties.stream().anyMatch(p -> p.getFullQualifiedName().contains("Properties.StaticProperty"))).isTrue();
        assertThat((int) properties.stream().filter(p -> p.getFullQualifiedName().contains("Invocations.Property")).count()).isEqualTo(2);
    }

}
