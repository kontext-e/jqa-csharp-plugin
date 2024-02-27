package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.RecordClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.RecordStructDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.StructDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TypeAnalyzerIT extends CSharpIntegrationTest{

    @Test
    @TestStore(reset = false)
    void TestClass(){
        ClassDescriptor clazz = (ClassDescriptor) queryForType("Project1.Types","TypeClass").get(0);

        assertThat(clazz.getInterfaces().size()).isEqualTo(0);
        assertThat(clazz.getSuperClass().getFullQualifiedName()).isEqualTo("object");
        assertThat(clazz.isStatic()).isFalse();
        assertThat(clazz.isSealed()).isTrue();
        assertThat(clazz.getTypeFragments().size()).isEqualTo(0);
        assertThat(clazz.getAccessibility()).isEqualTo("Public");
        assertThat(clazz.isPartial()).isFalse();
        assertThat(clazz.isAbstract()).isFalse();
        assertThat(clazz.getName()).isEqualTo("TypeClass");
        assertThat(clazz.getFullQualifiedName()).isEqualTo("Project1.Types.TypeClass");
        assertThat(clazz.getFirstLineNumber()).isEqualTo(5);
        assertThat(clazz.getLastLineNumber()).isEqualTo(20);
        assertThat(clazz.getEffectiveLineCount()).isEqualTo(15);

        List<MemberDescriptor> members = clazz.getDeclaredMembers();
        assertThat(members.size()).isEqualTo(6);
        assertThat(members.stream().filter(m-> m instanceof MethodDescriptor).count()).isEqualTo(4);
        assertThat(members.stream().filter(m-> m instanceof ConstructorDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> m instanceof PropertyDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> m instanceof FieldDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> (m instanceof MethodDescriptor) && ((MethodDescriptor) m).getAssociatedProperty() != null).count()).isEqualTo(2);
    }

    @Test
    @TestStore(reset = false)
    void TestInterface(){
        InterfaceTypeDescriptor interfaceDescriptor = (InterfaceTypeDescriptor) queryForType("Project1.Types","ITypeInterface").get(0);

        assertThat(interfaceDescriptor.getInterfaces().size()).isEqualTo(0);
        assertThat(interfaceDescriptor.getTypeFragments().size()).isEqualTo(0);
        assertThat(interfaceDescriptor.getAccessibility()).isEqualTo("Public");
        assertThat(interfaceDescriptor.isPartial()).isFalse();
        assertThat(interfaceDescriptor.getName()).isEqualTo("ITypeInterface");
        assertThat(interfaceDescriptor.getFullQualifiedName()).isEqualTo("Project1.Types.ITypeInterface");
        assertThat(interfaceDescriptor.getFirstLineNumber()).isEqualTo(5);
        assertThat(interfaceDescriptor.getLastLineNumber()).isEqualTo(13);
        assertThat(interfaceDescriptor.getEffectiveLineCount()).isEqualTo(8);

        List<MemberDescriptor> members = interfaceDescriptor.getDeclaredMembers();
        assertThat(members.size()).isEqualTo(4);
        assertThat(members.stream().filter(m-> m instanceof MethodDescriptor).count()).isEqualTo(3);
        assertThat(members.stream().filter(m-> m instanceof PropertyDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> (m instanceof MethodDescriptor) && ((MethodDescriptor) m).getAssociatedProperty() != null).count()).isEqualTo(2);
    }

    @Test
    @TestStore(reset = false)
    void TestStruct(){
        StructDescriptor struct = (StructDescriptor) queryForType("Project1.Types","TypeStruct").get(0);

        assertThat(struct.getTypeFragments().size()).isEqualTo(0);
        assertThat(struct.getAccessibility()).isEqualTo("Public");
        assertThat(struct.isPartial()).isFalse();
        assertThat(struct.getName()).isEqualTo("TypeStruct");
        assertThat(struct.getFullQualifiedName()).isEqualTo("Project1.Types.TypeStruct");
        assertThat(struct.isReadOnly()).isTrue();
        assertThat(struct.getFirstLineNumber()).isEqualTo(5);
        assertThat(struct.getLastLineNumber()).isEqualTo(19);
        assertThat(struct.getEffectiveLineCount()).isEqualTo(14);

        List<MemberDescriptor> members = struct.getDeclaredMembers();
        assertThat(members.size()).isEqualTo(7);
        assertThat(members.stream().filter(m-> m instanceof MethodDescriptor).count()).isEqualTo(5);
        assertThat(members.stream().filter(m-> m instanceof ConstructorDescriptor).count()).isEqualTo(2);
        assertThat(members.stream().filter(m-> m instanceof PropertyDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> m instanceof FieldDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> (m instanceof MethodDescriptor) && ((MethodDescriptor) m).getAssociatedProperty() != null).count()).isEqualTo(2);
    }

    @Test
    @TestStore(reset = false)
    void TestRecordClass(){
        RecordClassDescriptor record = (RecordClassDescriptor) queryForType("Project1.Types","TypeRecord").get(0);

        assertThat(record.getInterfaces().size()).isEqualTo(0);
        assertThat(record.getSuperClass().getFullQualifiedName()).isEqualTo("object");
        assertThat(record.isStatic()).isFalse();
        assertThat(record.isSealed()).isFalse();
        assertThat(record.getTypeFragments().size()).isEqualTo(0);
        assertThat(record.getAccessibility()).isEqualTo("Public");
        assertThat(record.isPartial()).isFalse();
        assertThat(record.isAbstract()).isFalse();
        assertThat(record.getName()).isEqualTo("TypeRecord");
        assertThat(record.getFullQualifiedName()).isEqualTo("Project1.Types.TypeRecord");
        assertThat(record.getFirstLineNumber()).isEqualTo(5);
        assertThat(record.getLastLineNumber()).isEqualTo(19);
        assertThat(record.getEffectiveLineCount()).isEqualTo(14);

        //TODO Is this wanted? EqualityContract Property etc?
        List<MemberDescriptor> members = record.getDeclaredMembers();
        assertThat(members.size()).isEqualTo(  9);
        assertThat(members.stream().filter(m-> m instanceof MethodDescriptor).count()).isEqualTo(6);
        assertThat(members.stream().filter(m-> m instanceof ConstructorDescriptor).count()).isEqualTo(2);
        assertThat(members.stream().filter(m-> m instanceof PropertyDescriptor).count()).isEqualTo(2);
        assertThat(members.stream().filter(m-> m instanceof FieldDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> (m instanceof MethodDescriptor) && ((MethodDescriptor) m).getAssociatedProperty() != null).count()).isEqualTo(3);
    }

    @Test
    @TestStore(reset = false)
    void TestRecordStruct(){
        RecordStructDescriptor recordStruct = (RecordStructDescriptor) queryForType("Project1.Types","TypeRecordStruct").get(0);

        assertThat(recordStruct.getTypeFragments().size()).isEqualTo(0);
        assertThat(recordStruct.getAccessibility()).isEqualTo("Public");
        assertThat(recordStruct.isPartial()).isFalse();
        assertThat(recordStruct.getName()).isEqualTo("TypeRecordStruct");
        assertThat(recordStruct.getFullQualifiedName()).isEqualTo("Project1.Types.TypeRecordStruct");
        assertThat(recordStruct.isReadOnly()).isFalse();
        assertThat(recordStruct.getFirstLineNumber()).isEqualTo(5);
        assertThat(recordStruct.getLastLineNumber()).isEqualTo(19);
        assertThat(recordStruct.getEffectiveLineCount()).isEqualTo(14);

        List<MemberDescriptor> members = recordStruct.getDeclaredMembers();
        assertThat(members.size()).isEqualTo(  7);
        assertThat(members.stream().filter(m-> m instanceof MethodDescriptor).count()).isEqualTo(5);
        assertThat(members.stream().filter(m-> m instanceof ConstructorDescriptor).count()).isEqualTo(2);
        assertThat(members.stream().filter(m-> m instanceof PropertyDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> m instanceof FieldDescriptor).count()).isEqualTo(1);
        assertThat(members.stream().filter(m-> (m instanceof MethodDescriptor) && ((MethodDescriptor) m).getAssociatedProperty() != null).count()).isEqualTo(2);
    }

    @Test
    @TestStore(reset = false)
    void TestEnumType(){
        EnumTypeDescriptor enumDescriptor = (EnumTypeDescriptor) queryForType("Project1.Types", "TypeEnum").get(0);

        assertThat(enumDescriptor.getAccessibility()).isEqualTo("Public");
        assertThat(enumDescriptor.isPartial()).isFalse();
        assertThat(enumDescriptor.getFullQualifiedName()).isEqualTo("Project1.Types.TypeEnum");
        assertThat(enumDescriptor.getFirstLineNumber()).isEqualTo(3);
        assertThat(enumDescriptor.getLastLineNumber()).isEqualTo(6);
        assertThat(enumDescriptor.getEffectiveLineCount()).isEqualTo(3);
    }

    @Test
    @TestStore(reset = false)
    void TestEnumValues(){
        List<EnumValueDescriptor> values = query("Match r=(v:Enum:Value)-[]-(t:Enum:Type) Where t.name=\"TypeEnum\" return v").getColumn("v");

        assertThat(values.size()).isEqualTo(4);
        assertThat(values.stream().anyMatch(value -> !value.getType().getName().equals("TypeEnum"))).isFalse();
        values.sort(Comparator.comparing(EnumValueDescriptor::getName));
        assertThat(values.get(0).getName()).isEqualTo("A");
        assertThat(values.get(1).getName()).isEqualTo("B");
        assertThat(values.get(2).getName()).isEqualTo("C");
        assertThat(values.get(3).getName()).isEqualTo("D");
    }


    @Test
    @TestStore(reset = false)
    void TestRecordDeclarationShorthand(){
        RecordClassDescriptor typeDescriptor = (RecordClassDescriptor) queryForType("Project1","OnePublicConstructor").get(0);
        assertThat(typeDescriptor.getDeclaredMembers().size()).isEqualTo(10);
        ConstructorDescriptor publicConstructor = (ConstructorDescriptor) typeDescriptor.getDeclaredMembers()
                .stream().filter(m -> m instanceof ConstructorDescriptor && m.getAccessibility().equals("Public")).collect(Collectors.toList()).get(0);
        assertThat(publicConstructor.getParameters().size()).isEqualTo(2);
        assertThat(publicConstructor.getName()).isEqualTo(".ctor");
        assertThat(publicConstructor.getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false)
    void testPrimaryConstructors(){
        List<MethodDescriptor> constructors = findConstructorsOfType("Constructors");

        assertThat(constructors.size()).isEqualTo(2);
        assertThat(constructors.stream().allMatch(c -> c instanceof ConstructorDescriptor)).isTrue();
        MethodDescriptor primaryConstructor = constructors.stream().filter(c -> c.getParameters().size() == 2).collect(Collectors.toList()).get(0);
//TODO        assertThat(primaryConstructor.getParameters().stream().allMatch(p -> p.getType().getName().equals("double"))).isTrue();
        assertThat(primaryConstructor.getParameters().size()).isEqualTo(2);
        MethodDescriptor explicitConstructor = constructors.stream().filter(c -> c.getParameters().isEmpty()).collect(Collectors.toList()).get(0);
        assertThat(explicitConstructor.getParameters().isEmpty()).isTrue();
    }


    @Test
    @TestStore(reset = false)
    void testDefaultConstructor(){
        List<MethodDescriptor> defaultConstructors = findConstructorsOfType("ClassWithDefaultConstructor");
        assertThat(defaultConstructors.size()).isEqualTo(1);
        assertThat(defaultConstructors.get(0).getParameters().isEmpty()).isTrue();
        assertThat(defaultConstructors.get(0).getAccessibility()).isEqualTo("Public");
    }

    @Test
    @TestStore(reset = false)
    void testClassWithoutConstructor(){
        ClassDescriptor clazz = (ClassDescriptor) query("Match (c:Class) Where c.name=\"ClassWithoutAnyConstructor\" Return c\n").getColumn("c").get(0);
        assertThat(clazz.getDeclaredMembers().isEmpty()).isTrue();
    }

    @Test
    @TestStore(reset = false)
    void testRecordStruct(){
        List<MethodDescriptor> constructors = findConstructorsOfType("TwoConstructors");
        assertThat(constructors.size()).isEqualTo(2);
        assertThat(constructors.stream().anyMatch(c -> c.getParameters().size() == 2)).isTrue();
        assertThat(constructors.stream().anyMatch(c -> c.getParameters().isEmpty())).isTrue();
        assertThat(constructors.stream().allMatch(c -> c.getAccessibility().equals("Public"))).isTrue();
    }

    private <T extends TypeDescriptor> List<T> queryForType(String namespace, String typeName){
        return query(
                String.format("Match (n:Namespace)-[]->(t:Type) Where n.fqn=\"%s\" And t.name=\"%s\" Return t",
                        namespace, typeName))
                .getColumn("t");
    }

    private List<MethodDescriptor> findConstructorsOfType(String nameOfClass){
        MemberOwningTypeDescriptor type = (MemberOwningTypeDescriptor) queryForType("Project1", nameOfClass).get(0);
        return type.getDeclaredMembers()
                .stream()
                .filter(m -> m instanceof ConstructorDescriptor)
                .map(m -> (MethodDescriptor) m)
                .collect(Collectors.toList());
    }

}
