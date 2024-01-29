package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class MethodAnalyzerIT extends CSharpIntegrationTest {

    @Test
    @TestStore(reset = false)
    void testMethodParameters(){
        MethodDescriptor method = queryForMethods("Methods", "MethodWithMultipleArguments").get(0);
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
    @TestStore(reset = false)
    void testProtectedInternalMethod() {
        MethodDescriptor method = queryForMethods("Methods", "ProtectedInternalMethod").get(0);
        assertThat(method.getAccessibility()).isEqualTo("ProtectedOrInternal");
    }

    @Test
    @TestStore(reset = false)
    void testMethodReturnType(){
        MethodDescriptor method = queryForMethods("Methods", "MethodWithReturnType").get(0);
        assertThat(method.getReturns().getFullQualifiedName()).isEqualTo("int");
    }

    @Test
    @TestStore(reset = false)
    void testImplicitlyPrivateMethods(){
        MethodDescriptor method = queryForMethods("Methods", "ImplicitlyPrivateMethod").get(0);
        assertThat(method.getAccessibility()).isEqualTo("Private");
    }

    @Test
    @TestStore(reset = false)
    void testExpressionMethod(){
        MethodDescriptor method = queryForMethods("Methods", "ExpressionMethod").get(0);
        assertThat(method.isImplementation()).isTrue();
        assertThat(method.getEffectiveLineCount()).isEqualTo(0);
    }

    @Test
    @TestStore(reset = false)
    void testExtensionMethod(){
        MethodDescriptor method = queryForMethods("MethodExtensions", "ExtensionMethod").get(0);
        assertThat(method.getExtendedType().getName()).isEqualTo("Methods");
    }

    @Test
    @TestStore(reset = false)
    void TestConstructor(){
        MethodDescriptor method = queryForMethods("Methods", "Methods").get(0);
        assertThat(method.getReturns().getFullQualifiedName()).isEqualTo("Project_1.Methods");
        assertThat(method instanceof ConstructorDescriptor).isTrue();
    }

    private List<MethodDescriptor> queryForMethods(String nameOfClass, String nameOfMethod){
        return query(
                String.format("Match (c:Class)-[]->(m:Method) Where c.name=\"%s\" And m.name=\"%s\" Return m",
                        nameOfClass, nameOfMethod))
                .getColumn("m");
    }



}
