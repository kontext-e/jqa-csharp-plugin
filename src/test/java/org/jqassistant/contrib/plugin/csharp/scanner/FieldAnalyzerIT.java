package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FieldAnalyzerIT extends CSharpIntegrationTest{

    @Test
    @TestStore(reset = false)
    void testPrivateField(){
        FieldDescriptor field = queryForField("privateField");

        assertThat(field.getAccessibility()).isEqualTo("Private");
        assertThat(field.isVolatile()).isFalse();
        assertThat(field.isStatic()).isFalse();
        assertThat(field.isRequired()).isFalse();
        assertThat(field.getTypes().size()).isEqualTo(1);
        assertThat(field.getTypes().get(0).getFullQualifiedName()).isEqualTo("string");
        assertThat(field.getFullQualifiedName()).isEqualTo("Project1.Fields.privateField");
        assertThat(field.getDeclaringType().getName()).isEqualTo("Fields");
    }

    @Test
    @TestStore(reset = false)
    void testPublicField(){
        FieldDescriptor field = queryForField("PublicField");
        assertThat(field.getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false)
    void testStaticField(){
        FieldDescriptor field = queryForField("StaticField");
        assertThat(field.isStatic()).isTrue();
        assertThat(field.getAccessibility()).isEqualTo("Internal");
        assertThat(field.getTypes().size()).isEqualTo(1);
        assertThat(field.getTypes().get(0).getName()).isEqualTo("Fields");
    }

    @Test
    @TestStore(reset = false)
    void testRequiredField(){
        FieldDescriptor field = queryForField("RequiredField");
        assertThat(field.isRequired()).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testVolatileField(){
        FieldDescriptor field = queryForField("VolatileField");
        assertThat(field.isVolatile()).isTrue();
        assertThat(field.getAccessibility()).isEqualTo("Protected");
    }

    @Test
    @TestStore(reset = false)
    void testFieldWithDefaultValue(){
        FieldDescriptor field = queryForField("DefaultField");
        assertThat(field.getValue().getValue()).isEqualTo("Initial String");
        assertThat(field.getAccessibility()).isEqualTo("ProtectedOrInternal");
    }

    private FieldDescriptor queryForField(String name){
        String query = String.format("Match (c:Class)-[]->(f:Field) Where c.name=\"Fields\" And f.name=\"%s\" Return f", name);
        List<FieldDescriptor> fields = query(query).getColumn("f");
        return fields.get(0);
    }

}
