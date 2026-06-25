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
    void testInvocationsOfMemberMethod(){
        MethodDescriptor method = (MethodDescriptor) query("Match (m:Method) where m.fqn=\"Project1.Invocations.Invocations()\" return m").getColumn("m").get(0);

        List<InvokesDescriptor> invokedBy = method.getInvokes();
        List<InvokesDescriptor> invocationsOfMethod = invokedBy.stream()
                .filter(m -> m.getInvokedMethod() != null && m.getInvokedMethod().getName() != null)
                .filter(m -> m.getInvokedMethod().getName().equals("Method"))
                .collect(Collectors.toList());
        assertThat(invocationsOfMethod.size()).isEqualTo(2);
        assertThat(invocationsOfMethod.stream().anyMatch(i->i.getLineNumber() == 16)).isTrue();
        assertThat(invocationsOfMethod.stream().anyMatch(i->i.getLineNumber() == 39)).isTrue();
    }

    @Test
    void testExtensionMethodInvocation(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Invocations.Method()")).isTrue();
    }

    @Test
    void testStaticMethodInvocation(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Types.TypeClass.ExtensionMethodWithArgument(double)")).isTrue();
    }

    @Test
    void testPropertyGetter(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Invocations.Property.get")).isTrue();
    }

    @Test
    void testPropertySetter(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Invocations.Property.set")).isTrue();
    }

    @Test
    void testPartialMethod(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "Project1.Partiality.PartialClass.PartialMethod()")).isTrue();
    }

    @Test
    void testConstructor(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(method.getInvokes()
                .stream()
                .anyMatch(i -> i.getInvokedMethod().getFullQualifiedName().equals("Project1.Types.TypeClass.TypeClass(string)"))
        ).isTrue();
    }

    @Test
    void testRecursion(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.ObjectCreations(Project1.Properties)");
        assertThat(containsCalledMethod(method, "Project1.Invocations.ObjectCreations(Project1.Properties)")).isTrue();
    }

    @Test
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
    void testConstructorCallToBase(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Constructors.Constructors()");
        assertThat(containsCalledMethod(method,"Project1.Constructors.Constructors(double, double)")).isTrue();
    }

    @Test
    void testCallToPartialClassConstructor(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.ObjectCreations(Project1.Properties)");
        assertThat(method.getInvokes()
                .stream()
                .map(InvokesDescriptor::getInvokedMethod)
                .filter(m->m.getFullQualifiedName().equals("Project1.Partiality.PartialClass.PartialClass()"))
                .count()
        ).isEqualTo(1);
    }

    @Test
    void testGenericMethodTypeParameters(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.InvocationOfGenericMethods()");

        List<InvokesDescriptor> genericMethodInvocation = method
                .getInvokes()
                .stream()
                .filter(m -> m.getInvokedMethod()
                        .getFullQualifiedName()
                        .equals("Project1.NonGenericClass.GenericMethodInNonGenericClass<T>(T)"))
                .collect(Collectors.toList());
        assertThat(genericMethodInvocation.size()).isEqualTo(1);

        List<TypeDescriptor> genericTypeArguments = genericMethodInvocation.get(0).getGenericTypeArguments();
        assertThat(genericTypeArguments.size()).isEqualTo(2);
        assertThat(genericTypeArguments.stream().anyMatch(i->i.getFullQualifiedName().equals("Project1.Properties"))).isTrue();
        assertThat(genericTypeArguments.stream().anyMatch(i->i.getFullQualifiedName().equals("System.Collections.Generic.List<T>"))).isTrue();
    }

    private static boolean containsCalledMethod(MethodDescriptor method, String methodName) {
        return method.getInvokes()
                .stream()
                .anyMatch(invokesDescriptor -> invokesDescriptor.getInvokedMethod().getFullQualifiedName().equals(methodName));
    }

    private MethodDescriptor queryForMethodInvocation(String methodName){
        return (MethodDescriptor) query(String.format(
            "Match (m:Method)-[:INVOKES]-(:Invocation)-[:INVOKES]-(:Method) where m.fqn=\"%s\" return m",
            methodName)
        ).getColumn("m")
                .get(0);
    }

}
