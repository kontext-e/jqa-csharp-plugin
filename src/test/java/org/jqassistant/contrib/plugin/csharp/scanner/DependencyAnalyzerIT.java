package org.jqassistant.contrib.plugin.csharp.scanner;

import org.assertj.core.api.Assertions;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.RecordClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class DependencyAnalyzerIT extends CSharpIntegrationTest{

    @Test
    @TestStore(reset = false)
    void testClassInheritance(){
        ClassDescriptor firstChildClass = (ClassDescriptor) queryForType("FirstChildClass").get(0);
        ClassDescriptor secondChildClass = (ClassDescriptor) queryForType("SecondChildClass").get(0);

        assertThat(firstChildClass.getSuperClass().getName()).isEqualTo("ParentClass");
        assertThat(secondChildClass.getSuperClass().getName()).isEqualTo("ParentClass");
    }

    @Test
    @TestStore(reset = false)
    void testRecordInheritance(){
        RecordClassDescriptor firstChildRecord = (RecordClassDescriptor) queryForType("FirstChildRecord").get(0);
        RecordClassDescriptor secondChildRecord = (RecordClassDescriptor) queryForType("SecondChildRecord").get(0);

        assertThat(firstChildRecord.getSuperClass().getName()).isEqualTo("ParentRecord");
        assertThat(secondChildRecord.getSuperClass().getName()).isEqualTo("ParentRecord");
    }

    @Test
    @TestStore(reset = false)
    void testInterfaceImplementation(){
        InterfaceTypeDescriptor parentInterface1 = (InterfaceTypeDescriptor) queryForType("IFirstParentInterface").get(0);
        InterfaceTypeDescriptor parentInterface2 = (InterfaceTypeDescriptor) queryForType("ISecondParentInterface").get(0);
        InterfaceTypeDescriptor childInterface1 = (InterfaceTypeDescriptor) queryForType("IFirstChildInterface").get(0);
        InterfaceTypeDescriptor childInterface2 = (InterfaceTypeDescriptor) queryForType("ISecondChildInterface").get(0);

        assertThat(childInterface1.getInterfaces().size()).isEqualTo(2);
        assertThat(childInterface2.getInterfaces().size()).isEqualTo(2);
        assertThat(childInterface1.getInterfaces().contains(parentInterface1)).isTrue();
        assertThat(childInterface1.getInterfaces().contains(parentInterface2)).isTrue();
        assertThat(childInterface2.getInterfaces().contains(parentInterface1)).isTrue();
        assertThat(childInterface2.getInterfaces().contains(parentInterface2)).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testImplementationAndInheritance(){
        ClassDescriptor child = (ClassDescriptor) queryForType("Child").get(0);

        assertThat(child.getSuperClass().getName()).isEqualTo("BaseClass");
        assertThat(child.getInterfaces().size()).isEqualTo(1);
        assertThat(child.getInterfaces().get(0).getName()).isEqualTo("IBaseInterface");
    }

    @Test
    @TestStore(reset = false)
    void testUsings(){
        List<NamespaceDescriptor> namespaces = query("MATCH (:File {name: \"FileWithUsings.cs\"})-[:USES]->(n:Namespace) RETURN n").getColumn("n");
        assertThat(namespaces.size()).isEqualTo(3);
        assertThat(namespaces.stream().anyMatch(n->n.getFullQualifiedName().equals("System"))).isTrue();
        assertThat(namespaces.stream().anyMatch(n->n.getFullQualifiedName().equals("System.Math"))).isTrue();
        assertThat(namespaces.stream().anyMatch(n->n.getFullQualifiedName().equals("System.Collections.Generic"))).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testUsingAlias(){
        List<UsesNamespaceDescriptor> usesNamespaceDescriptorList = query("MATCH (:File {name: \"FileWithUsings.cs\"})-[u:USES {alias: 'MyAlias'}]->() RETURN u").getColumn("u");
        Assertions.assertThat(usesNamespaceDescriptorList).hasSize(1);
    }

    private <T extends TypeDescriptor> List<T> queryForType(String typeName){
        return query(
                String.format("Match (n:Namespace)-[]->(t:Type) Where n.fqn=\"Project1.TypeDependencies\" And t.name=\"%s\" Return t",
                        typeName))
                .getColumn("t");
    }

}
