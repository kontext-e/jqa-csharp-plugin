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
        List<MethodDescriptor> methods = query("Match (c:Class)-[]->(m:Method) Where c.name=\"Methods\" And m.name=\"MethodWithDefaultArguments\" Return m").getColumn("m");

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
    void testUsings() {
        List<MethodDescriptor> methods = query("Match (c:Class)-[]->(m:Method) Where c.name=\"Methods\" And m.name=\"ProtectedInternalMethod\" Return m").getColumn("m");

        assertThat(methods.size()).isEqualTo(1);
        MethodDescriptor method = methods.get(0);
        assertThat(method.getAccessibility()).isEqualTo("ProtectedOrInternal");
    }



}
