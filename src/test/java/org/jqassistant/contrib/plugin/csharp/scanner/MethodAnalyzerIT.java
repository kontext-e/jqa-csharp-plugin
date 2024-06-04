package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class MethodAnalyzerIT extends CSharpIntegrationTest {

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
        assertThat(method.getReturns().get(0).getFullQualifiedName()).isEqualTo("int");
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
        assertThat(method.isExtensionMethod()).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void TestConstructor(){
        MethodDescriptor method = queryForMethods("Methods", ".ctor").get(0);
        assertThat(method.getReturns().get(0).getFullQualifiedName()).isEqualTo("Project1.Methods");
        assertThat(method instanceof ConstructorDescriptor).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testCyclomaticComplexity(){
        MethodDescriptor method = queryForMethods("CyclomaticComplexityExample", "shouldBeOne").get(0);
        assertThat(method.getCyclomaticComplexity()).isEqualTo(1);
    }

    @Test
    @TestStore(reset = false)
    void testCyclomaticComplexityBranch(){
        MethodDescriptor method = queryForMethods("CyclomaticComplexityExample", "ShouldBeTwo").get(0);
        assertThat(method.getCyclomaticComplexity()).isEqualTo(2);
    }

    @Test
    @TestStore(reset = false)
    void testCyclomaticComplexityLoop(){
        MethodDescriptor method = queryForMethods("CyclomaticComplexityExample", "ShouldBeTwoAsWell").get(0);
        assertThat(method.getCyclomaticComplexity()).isEqualTo(2);
    }


    @Test
    @TestStore(reset = false)
    void testCyclomaticComplexityThree(){
        MethodDescriptor method = queryForMethods("CyclomaticComplexityExample", "ShouldBeThree").get(0);
        assertThat(method.getCyclomaticComplexity()).isEqualTo(3);
    }


    private List<MethodDescriptor> queryForMethods(String nameOfClass, String nameOfMethod){
        return query(
                String.format("Match (c:Class)-[]->(m:Method) Where c.name=\"%s\" And m.name=\"%s\" Return m",
                        nameOfClass, nameOfMethod))
                .getColumn("m");
    }
}
