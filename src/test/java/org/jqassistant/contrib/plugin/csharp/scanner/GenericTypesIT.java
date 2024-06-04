package org.jqassistant.contrib.plugin.csharp.scanner;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GenericTypesIT extends CSharpIntegrationTest {

    @Test
    @TestStore(reset = false)
    void testNestedGenericField(){
        List<String> typesOfField = query(
                "MATCH (field:Field)-[:OF_TYPE]-(type:Type) " +
                "WHERE field.name='NestedGenericField' " +
                "RETURN type.fqn as t").getColumn("t");

        assertThat(typesOfField.size()).isEqualTo(8);
        assertThat(typesOfField.contains("T")).isTrue();
        assertThat(typesOfField.contains("TS")).isTrue();
        assertThat(typesOfField.contains("TS[]")).isTrue();
        assertThat(typesOfField.contains("string")).isTrue();
        assertThat(typesOfField.contains("string[]")).isTrue();
        assertThat(typesOfField.contains("Project1.Properties")).isTrue();
        assertThat(typesOfField.contains("System.Collections.Generic.List<T>")).isTrue();
        assertThat(typesOfField.contains("System.Collections.Generic.Dictionary<TKey, TValue>?")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testGenericFieldWithoutConstraint(){
        List<String> typesOfField = query(
                "MATCH (field:Field)-[:OF_TYPE]-(type:Type) " +
                        "WHERE field.name='GenericFieldWithoutConstraint' " +
                        "RETURN type.fqn as t").getColumn("t");

        assertThat(typesOfField.size()).isEqualTo(1);
        assertThat(typesOfField.contains("T")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testMethodWithNestedGenericReturnType(){
        List<String> returnTypes = query(
                "MATCH (method:Method)-[:RETURNS]-(type:Type) " +
                "WHERE method.name='MethodWithNestedGenericReturnType' " +
                "RETURN type.fqn as t").getColumn("t");

        assertThat(returnTypes.size()).isEqualTo(5);
        assertThat(returnTypes.contains("T")).isTrue();
        assertThat(returnTypes.contains("TS")).isTrue();
        assertThat(returnTypes.contains("Project1.Properties")).isTrue();
        assertThat(returnTypes.contains("System.Collections.Generic.List<T>")).isTrue();
        assertThat(returnTypes.contains("System.Collections.Generic.List<T>?")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testGenericMethodInNonGenericClass(){
        List<String> returnTypes = query(
                "MATCH (method:Method)-[:RETURNS]-(type:Type) " +
                "WHERE method.name='GenericMethodInNonGenericClass' " +
                "RETURN type.fqn as t").getColumn("t");

        assertThat(returnTypes.size()).isEqualTo(1);
        assertThat(returnTypes.contains("T")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testGenericMethodInNonGenericClassWithConstraints(){
        List<String> returnTypes = query(
                "MATCH (method:Method)-[:RETURNS]-(type:Type) " +
                "WHERE method.name='GenericMethodInNonGenericClassWithConstraints' " +
                "RETURN type.fqn as t").getColumn("t");

        assertThat(returnTypes.size()).isEqualTo(2);
        assertThat(returnTypes.contains("T")).isTrue();
        assertThat(returnTypes.contains("Project1.Properties")).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testRecursiveGenericMethod(){
        List<String> returnTypes = query(
                "MATCH (method:Method)-[:RETURNS]-(type:Type) " +
                "WHERE method.name='RecursiveGenericMethod' " +
                "RETURN type.fqn as t").getColumn("t");

        assertThat(returnTypes.size()).isEqualTo(2);
        assertThat(returnTypes.contains("TRec")).isTrue();
        assertThat(returnTypes.contains("Project1.EvenMoreGeneric<T>")).isTrue();
    }


    @Test
    @TestStore(reset = false)
    void testParameterWithNestedGenericType(){
        List<String> parameterTypes = query(
                "MATCH (method:Method)-[:HAS]-(p:Parameter)-[:OF_TYPE]-(type:Type) " +
                "WHERE method.name='MethodWithNestedGenericReturnType' " +
                "AND p.name='parameter' "+
                "RETURN type.fqn as t"
        ).getColumn("t");

        assertThat(parameterTypes.size()).isEqualTo(4);
        assertThat(parameterTypes).contains("T");
        assertThat(parameterTypes).contains("TS");
        assertThat(parameterTypes).contains("Project1.Properties");
        assertThat(parameterTypes).contains("System.Collections.Generic.List<T>");
    }
}
