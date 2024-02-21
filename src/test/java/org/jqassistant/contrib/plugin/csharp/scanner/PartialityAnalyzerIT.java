package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class PartialityAnalyzerIT extends CSharpIntegrationTest{

    @Test
    @TestStore(reset = false)
    void testPartialClass(){
        List<TypeDescriptor> partialClasses = queryForType("PartialClass");
        assertPartiality(partialClasses);
    }

    @Test
    @TestStore(reset = false)
    void testPartialInterface(){
        List<TypeDescriptor> partialClasses = queryForType("IPartialInterface");
        assertPartiality(partialClasses);
    }

    @Test
    @TestStore(reset = false)
    void testPartialStruct(){
        List<TypeDescriptor> partialClasses = queryForType("PartialStruct");
        assertPartiality(partialClasses);
    }

    @Test
    @TestStore(reset = false)
    void testPartialRecord(){
        List<TypeDescriptor> partialClasses = queryForType("PartialRecord");
        assertPartiality(partialClasses);
    }

    @Test
    @TestStore(reset = false)
    void testPartialMethods(){
        List<MethodDescriptor> partialMethods = query("Match (c:Class)-[]-(m:Method) where c.fqn=\"Project1.Partiality.PartialClass\" return m").getColumn("m");

        assertThat(partialMethods.size()).isEqualTo(3);
        List<MethodDescriptor> implementedMethods = partialMethods.stream().filter(m -> m.getName().equals("PartialMethod")).collect(Collectors.toList());
        Optional<MethodDescriptor> implementation = implementedMethods.stream().filter(MethodDescriptor::isImplementation).findAny();
        Optional<MethodDescriptor> reference = implementedMethods.stream().filter(m -> !m.isImplementation()).findAny();

        assertThat(implementedMethods.size()).isEqualTo(2);
        assertThat(implementation.isPresent()).isTrue();
        assertThat(implementation.get().isPartial()).isTrue();

        assertThat(reference.isPresent()).isTrue();
        assertThat(reference.get().isPartial()).isTrue();
        assertThat(implementation.get().getMethodFragments().contains(reference.get())).isTrue();

        Optional<MethodDescriptor> unimplementedMethod = partialMethods.stream().filter(m -> m.getName().equals("UnimplementedMethod")).findAny();
        assertThat(unimplementedMethod.isPresent()).isTrue();
        assertThat(unimplementedMethod.get().isPartial()).isTrue();
        assertThat(unimplementedMethod.get().isImplementation()).isFalse();
        assertThat(unimplementedMethod.get().getMethodFragments().isEmpty()).isTrue();
    }

    private static void assertPartiality(List<TypeDescriptor> partialType) {
        assertThat(partialType.size()).isEqualTo(2);
        assertThat(partialType.get(0).getTypeFragments().contains(partialType.get(1))).isTrue();
        assertThat(partialType.get(1).getTypeFragments().contains(partialType.get(0))).isTrue();

        assertThat(partialType.get(0).isPartial()).isTrue();
        assertThat(partialType.get(1).isPartial()).isTrue();
    }

    private <T extends TypeDescriptor> List<T> queryForType(String typeName){
        return query(
                String.format("Match (n:Namespace)-[]->(t:Type) Where n.fqn=\"Project1.Partiality\" And t.name=\"%s\" Return t",
                        typeName))
                .getColumn("t");
    }

}
