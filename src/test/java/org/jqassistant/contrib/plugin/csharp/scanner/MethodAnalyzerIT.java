package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class MethodAnalyzerIT extends CSharpIntegrationTest {

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testMethodParameters(){
        List<MethodDescriptor> methods = queryForMethods("MethodWithMultipleArguments");

        assertThat(methods.size()).isEqualTo(1);
        MethodDescriptor method = methods.get(0);
        assertThat(method.getParameters().size()).isEqualTo(2);
        List<ParameterDescriptor> parameters = method.getParameters();
        List<ParameterDescriptor> parameterDescriptors = new ArrayList<>(parameters);
        parameterDescriptors.sort(Comparator.comparingInt(ParameterDescriptor::getIndex));
        assertThat(parameterDescriptors.get(0).getIndex()).isEqualTo(0);
        assertThat(parameterDescriptors.get(1).getIndex()).isEqualTo(1);
        assertThat(parameterDescriptors.get(0).getName()).isEqualTo("methods");
        assertThat(parameterDescriptors.get(1).getName()).isEqualTo("number");
        assertThat(parameterDescriptors.get(0).getType().getName()).isEqualTo("Methods");
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testProtectedInternalMethod() {
        MethodDescriptor method = queryForMethods("ProtectedInternalMethod").get(0);
        assertThat(method.getAccessibility()).isEqualTo("ProtectedOrInternal");
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testMethodReturnType(){
        MethodDescriptor method = queryForMethods("MethodWithReturnType").get(0);
        assertThat(method.getReturns().getFullQualifiedName()).isEqualTo("int");
    }

    @Test
    @TestStore(reset = false, type = TestStore.Type.FILE)
    void testImplicitlyPrivateMethods(){
        MethodDescriptor method = queryForMethods("ImplicitlyPrivateMethod").get(0);
        assertThat(method.getAccessibility()).isEqualTo("Private");
    }

    private List<MethodDescriptor> queryForMethods(String nameOfMethod){
        return query(
                String.format("Match (c:Class)-[]->(m:Method) Where c.name=\"Methods\" And m.name=\"%s\" Return m",
                        nameOfMethod))
                .getColumn("m");
    }



}
