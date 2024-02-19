package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
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
        // WORK IN PROGRESS assertThat(invokedMethods.size()).isEqualTo(2);
        assertThat(invokedMethods.stream().anyMatch(i -> i.getName().equals("TypeClass"))).isTrue();
        assertThat(invokedMethods.stream().anyMatch(i -> i.getName().equals("TypeStruct"))).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testMethodInvocations(){
        List<MethodDescriptor> invokedMethods = query("Match (m:Method)-[:INVOKES]-(n:Method) Where m.name=\"Invocations\" Return n").getColumn("n");

        List<MethodDescriptor> methods = invokedMethods.stream().filter(m -> !(m instanceof ConstructorDescriptor)).collect(Collectors.toList());
        //WORK IN PROGRESS assertThat(methods.size()).isEqualTo(5);
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

    @Test
    @TestStore(reset = false)
    void testObjectCreations(){
        List<InvokesDescriptor> invocations = query("Match r=(m:Method)-[i:INVOKES]-(c:Constructor) where m.name=\"Invocations\" and c.name=\"Properties()\" Return i").getColumn("i");
        assertThat(invocations.size()).isEqualTo(4);
        assertThat(invocations.stream().anyMatch(invokesDescriptor -> invokesDescriptor.getLineNumber() == 37)).isTrue();
        assertThat(invocations.stream().anyMatch(invokesDescriptor -> invokesDescriptor.getLineNumber() == 38)).isTrue();
        assertThat(invocations.stream().anyMatch(invokesDescriptor -> invokesDescriptor.getLineNumber() == 39)).isTrue();
        assertThat(invocations.stream().anyMatch(invokesDescriptor -> invokesDescriptor.getLineNumber() == 40)).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testConstructorCallToPrimaryConstructor(){
        InvokesDescriptor invokesDescriptor = (InvokesDescriptor) query("Match r=(k:Class)-[:DECLARES]-(c:Constructor)-[i:INVOKES]-(d:Constructor) where c.name=d.name return i").getColumn("i").get(0);
        assertThat(invokesDescriptor.getLineNumber()).isEqualTo(10);
        assertThat(invokesDescriptor.getInvokingMethod().getName()).isEqualTo("Constructors");
        assertThat(invokesDescriptor.getInvokedMethod().getName()).isEqualTo("Constructors");
        assertThat(invokesDescriptor.getInvokedMethod().getFullQualifiedName()).endsWith("Constructors(double, double)");
    }

}
