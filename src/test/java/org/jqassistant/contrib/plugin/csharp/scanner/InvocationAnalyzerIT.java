package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.ArrayCreationDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class InvocationAnalyzerIT extends CSharpIntegrationTest {

    @Test
    @TestStore(reset = false)
    void testInvocationsOfMemberMethod(){
        MethodDescriptor method = (MethodDescriptor) query("Match (m:Method)-[:INVOKES]-(:Method) where m.name=\"Method\" return m").getColumn("m").get(0);

        List<InvokesDescriptor> invokedBy = method.getInvokedBy();
        assertThat(invokedBy.size()).isEqualTo(2);
        assertThat(invokedBy.stream().allMatch(m->m.getInvokingMethod().getFullQualifiedName().equals("Project1.Invocations.Invocations()"))).isTrue();
        assertThat(invokedBy.stream().anyMatch(i->i.getLineNumber() == 16)).isTrue();
        assertThat(invokedBy.stream().anyMatch(i->i.getLineNumber() == 39)).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testExtensionMethodInvocation(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Invocations.Method()")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testStaticMethodInvocation(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.ExtensionMethods.Extensions.ExtensionMethodWithArgument(Project1.Types.TypeClass, double)")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testPropertyGetter(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Invocations.Property.get")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testPropertySetter(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Invocations.Property.get")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testPartialMethod(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Partiality.PartialClass.PartialMethod()")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testConstructor(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(method.getInvokes()
                .stream()
                .anyMatch(i -> i.getInvokedMethod().getFullQualifiedName().equals("Project1.Types.TypeClass.TypeClass(string)"))
        ).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testRecursion(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.ObjectCreations(Project1.Properties)");
        assertThat(containsCalledMethod(method, "Project1.Invocations.ObjectCreations(Project1.Properties)")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testConstructors(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.ObjectCreations(Project1.Properties)");
        assertThat(method.getInvokes()
                .stream()
                .map(InvokesDescriptor::getInvokedMethod)
                .filter(m->m.getFullQualifiedName().equals("Project1.Properties.Properties()"))
                .count()
        ).isEqualTo(4);
    }

    @Test
    @TestStore(reset = false)
    void testTwoCallsToSameMethod(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(method.getInvokes()
                .stream()
                .map(InvokesDescriptor::getInvokedMethod)
                .filter(m->m.getFullQualifiedName().equals("Project1.Invocations.Method()"))
                .count()
        ).isEqualTo(2);
    }

    @Test
    @TestStore(reset = false)
    void testArrayCreations(){
        MethodDescriptor method = (MethodDescriptor) query("Match (m:Method)-[:CREATES_ARRAY]-(:Type) where m.fqn=\"Project1.Invocations.ArrayCreations()\" return m")
                .getColumn("m").get(0);
        List<TypeDescriptor> createdTypes = method.getCreatesArray().stream().map(ArrayCreationDescriptor::getCreatedType).collect(Collectors.toList());
        assertThat(createdTypes.size()).isEqualTo(6);
        assertThat(createdTypes.stream().filter(t -> t.getFullQualifiedName().equals("int[]")).count()).isEqualTo(2);
        assertThat(createdTypes.stream().filter(t -> t.getFullQualifiedName().equals("int[*,*]")).count()).isEqualTo(2);
        assertThat(createdTypes.stream().filter(t -> t.getFullQualifiedName().equals("string?[]")).count()).isEqualTo(1);
        assertThat(createdTypes.stream().filter(t -> t.getFullQualifiedName().equals("int[][]")).count()).isEqualTo(1);
    }

    @Test
    @TestStore(reset = false)
    void testConstructorCallToBase(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Constructors.Constructors()");
        assertThat(containsCalledMethod(method,"Project1.Constructors.Constructors(double, double)")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testCallToPartialClassConstructor(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.ObjectCreations(Project1.Properties)");
        assertThat(method.getInvokes()
                .stream()
                .map(InvokesDescriptor::getInvokedMethod)
                .filter(m->m.getFullQualifiedName().equals("Project1.Partiality.PartialClass.PartialClass()"))
                .count()
        ).isEqualTo(1);
    }

    private static boolean containsCalledMethod(MethodDescriptor method, String methodName) {
        return method.getInvokes()
                .stream()
                .anyMatch(invokesDescriptor -> invokesDescriptor.getInvokedMethod().getFullQualifiedName().equals(methodName));
    }

    private MethodDescriptor queryForMethodInvocation(String methodName){
        return (MethodDescriptor) query(String.format(
            "Match (m:Method)-[:INVOKES]-(:Method) where m.fqn=\"%s\" return m",
            methodName)
        ).getColumn("m")
                .get(0);
    }

}
