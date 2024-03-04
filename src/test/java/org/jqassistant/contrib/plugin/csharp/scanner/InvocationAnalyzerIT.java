package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

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
        assertThat(containsCalledMethod(method, "Method")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testStaticMethodInvocation(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "ExtensionMethodWithArgument")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testPropertyGetter(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "get_Property")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testPropertySetter(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "set_Property")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testPartialMethod(){
        MethodDescriptor method = queryForMethodInvocation("Project1.Invocations.Invocations()");
        assertThat(containsCalledMethod(method, "PartialMethod")).isTrue();
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
        assertThat(containsCalledMethod(method, "ObjectCreations")).isTrue();
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

    private static boolean containsCalledMethod(MethodDescriptor method, String Method) {
        return method.getInvokes()
                .stream()
                .anyMatch(invokesDescriptor -> invokesDescriptor.getInvokedMethod().getName().equals(Method));
    }

    private MethodDescriptor queryForMethodInvocation(String methodName){
        return (MethodDescriptor) query(String.format(
            "Match (m:Method)-[:INVOKES]-(:Method) where m.fqn=\"%s\" return m",
            methodName)
        ).getColumn("m")
                .get(0);
    }

}
