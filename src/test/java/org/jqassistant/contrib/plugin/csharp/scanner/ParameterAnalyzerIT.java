package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ParameterAnalyzerIT extends CSharpIntegrationTest {

    @Test
    @TestStore(reset = false)
    void testMethodZeroParameters(){
        MethodDescriptor method = (MethodDescriptor) query("Match (m:Method) where m.name=\"MethodWithLocalMethod\" return m").getColumn("m").get(0);
        assertThat(method.getParameters().size()).isEqualTo(0);
    }

    @Test
    @TestStore(reset = false)
    void testMethodParameter(){
        MethodDescriptor method = (MethodDescriptor) query("Match (m:Method)-[]-(p:Parameter)-[]-(t:Type) where m.name=\"ExpressionMethod\" return m").getColumn("m").get(0);
        assertThat(method.getParameters().size()).isEqualTo(1);
        ParameterDescriptor parameter = method.getParameters().get(0);
        assertThat(parameter.getName()).isEqualTo("text");
        assertThat(parameter.getIndex()).isEqualTo(0);
    }

    @Test
    @TestStore(reset = false)
    void testMethodTwoParameters(){
        MethodDescriptor method = (MethodDescriptor) query("Match (m:Method) where m.name=\"MethodWithMultipleArguments\" return m").getColumn("m").get(0);
        assertThat(method.getParameters().size()).isEqualTo(2);
        List<ParameterDescriptor> parameters = method.getParameters();
        List<ParameterDescriptor> parameterDescriptors = new ArrayList<>(parameters);
        parameterDescriptors.sort(Comparator.comparingInt(ParameterDescriptor::getIndex));
        assertThat(parameterDescriptors.get(0).getIndex()).isEqualTo(0);
        assertThat(parameterDescriptors.get(1).getIndex()).isEqualTo(1);
        assertThat(parameterDescriptors.get(0).getName()).isEqualTo("methods");
        assertThat(parameterDescriptors.get(1).getName()).isEqualTo("number");
        assertThat(parameterDescriptors.get(0).getTypes().size()).isEqualTo(1);
        assertThat(parameterDescriptors.get(0).getTypes().get(0).getName()).isEqualTo("Methods");
    }
}
