package org.jqassistant.contrib.plugin.csharp.scanner;

import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.RecordClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.RecordStructDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.StructDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TypeAnalyzerIT extends CSharpIntegrationTest{

    @Test
    @TestStore(reset = false)
    void TestClass(){
        ClassDescriptor clazz = (ClassDescriptor) queryForType("TypeClass").get(0);

        assertThat(clazz.getInterfaces().size()).isEqualTo(0);
        assertThat(clazz.getSuperClass().getFullQualifiedName()).isEqualTo("object");
        assertThat(clazz.isStatic()).isFalse();
        assertThat(clazz.isSealed()).isTrue();
        assertThat(clazz.getClassFragments().size()).isEqualTo(0);
        assertThat(clazz.getAccessibility()).isEqualTo("Public");
        assertThat(clazz.getPartial()).isFalse();
        assertThat(clazz.isAbstract()).isFalse();
        assertThat(clazz.getName()).isEqualTo("TypeClass");
        assertThat(clazz.getFullQualifiedName()).isEqualTo("Project1.Types.TypeClass");

        assertThat(clazz.getDeclaredMembers().size()).isEqualTo(4);
        List<MemberDescriptor> members = new ArrayList<>(clazz.getDeclaredMembers());
        members.sort(Comparator.comparing(MemberDescriptor::getName));
        assertThat(members.get(0)).isInstanceOf(MethodDescriptor.class);
        assertThat(members.get(1)).isInstanceOf(PropertyDescriptor.class);
        assertThat(members.get(2)).isInstanceOf(ConstructorDescriptor.class);
        assertThat(members.get(3)).isInstanceOf(FieldDescriptor.class);
    }

    @Test
    @TestStore(reset = false)
    void TestInterface(){
        InterfaceTypeDescriptor interfaceDescriptor = (InterfaceTypeDescriptor) queryForType("ITypeInterface").get(0);

        assertThat(interfaceDescriptor.getInterfaces().size()).isEqualTo(0);
        assertThat(interfaceDescriptor.getClassFragments().size()).isEqualTo(0);
        assertThat(interfaceDescriptor.getAccessibility()).isEqualTo("Public");
        assertThat(interfaceDescriptor.getPartial()).isFalse();
        assertThat(interfaceDescriptor.getName()).isEqualTo("ITypeInterface");
        assertThat(interfaceDescriptor.getFullQualifiedName()).isEqualTo("Project1.Types.ITypeInterface");

        assertThat(interfaceDescriptor.getDeclaredMembers().size()).isEqualTo(2);
        List<MemberDescriptor> members = new ArrayList<>(interfaceDescriptor.getDeclaredMembers());
        members.sort(Comparator.comparing(MemberDescriptor::getName));
        assertThat(members.get(0)).isInstanceOf(MethodDescriptor.class);
        assertThat(members.get(1)).isInstanceOf(PropertyDescriptor.class);
    }

    @Test
    @TestStore(reset = false)
    void TestStruct(){
        StructDescriptor struct = (StructDescriptor) queryForType("TypeStruct").get(0);

        assertThat(struct.getClassFragments().size()).isEqualTo(0);
        assertThat(struct.getAccessibility()).isEqualTo("Public");
        assertThat(struct.getPartial()).isFalse();
        assertThat(struct.getName()).isEqualTo("TypeStruct");
        assertThat(struct.getFullQualifiedName()).isEqualTo("Project1.Types.TypeStruct");
        assertThat(struct.isReadOnly()).isTrue();

        assertThat(struct.getDeclaredMembers().size()).isEqualTo(4);
        List<MemberDescriptor> members = new ArrayList<>(struct.getDeclaredMembers());
        members.sort(Comparator.comparing(MemberDescriptor::getName));
        assertThat(members.get(0)).isInstanceOf(MethodDescriptor.class);
        assertThat(members.get(1)).isInstanceOf(PropertyDescriptor.class);
        assertThat(members.get(2)).isInstanceOf(ConstructorDescriptor.class);
        assertThat(members.get(3)).isInstanceOf(FieldDescriptor.class);
    }

    @Test
    @TestStore(reset = false)
    void TestRecordClass(){
        RecordClassDescriptor record = (RecordClassDescriptor) queryForType("TypeRecord").get(0);

        assertThat(record.getInterfaces().size()).isEqualTo(0);
        assertThat(record.getSuperClass().getFullQualifiedName()).isEqualTo("object");
        assertThat(record.isStatic()).isFalse();
        assertThat(record.isSealed()).isFalse();
        assertThat(record.getClassFragments().size()).isEqualTo(0);
        assertThat(record.getAccessibility()).isEqualTo("Public");
        assertThat(record.getPartial()).isFalse();
        assertThat(record.isAbstract()).isFalse();
        assertThat(record.getName()).isEqualTo("TypeRecord");
        assertThat(record.getFullQualifiedName()).isEqualTo("Project1.Types.TypeRecord");

        assertThat(record.getDeclaredMembers().size()).isEqualTo(4);
        List<MemberDescriptor> members = new ArrayList<>(record.getDeclaredMembers());
        members.sort(Comparator.comparing(MemberDescriptor::getName));
        assertThat(members.get(0)).isInstanceOf(MethodDescriptor.class);
        assertThat(members.get(1)).isInstanceOf(PropertyDescriptor.class);
        assertThat(members.get(2)).isInstanceOf(ConstructorDescriptor.class);
        assertThat(members.get(3)).isInstanceOf(FieldDescriptor.class);
    }

    @Test
    @TestStore(reset = false)
    void TestRecordStruct(){
        TypeDescriptor typeDescriptor = queryForType("TypeRecordStruct").get(0);
        RecordStructDescriptor recordStruct = (RecordStructDescriptor) typeDescriptor;

        assertThat(recordStruct.getClassFragments().size()).isEqualTo(0);
        assertThat(recordStruct.getAccessibility()).isEqualTo("Public");
        assertThat(recordStruct.getPartial()).isFalse();
        assertThat(recordStruct.getName()).isEqualTo("TypeRecordStruct");
        assertThat(recordStruct.getFullQualifiedName()).isEqualTo("Project1.Types.TypeRecordStruct");
        assertThat(recordStruct.isReadOnly()).isFalse();

        assertThat(recordStruct.getDeclaredMembers().size()).isEqualTo(4);
        List<MemberDescriptor> members = new ArrayList<>(recordStruct.getDeclaredMembers());
        members.sort(Comparator.comparing(MemberDescriptor::getName));
        assertThat(members.get(0)).isInstanceOf(MethodDescriptor.class);
        assertThat(members.get(1)).isInstanceOf(PropertyDescriptor.class);
        assertThat(members.get(2)).isInstanceOf(ConstructorDescriptor.class);
        assertThat(members.get(3)).isInstanceOf(FieldDescriptor.class);
    }

    private <T extends TypeDescriptor> List<T> queryForType(String typeName){
        return query(
                String.format("Match (n:Namespace)-[]->(t:Type) Where n.fqn=\"Project1.Types\" And t.name=\"%s\" Return t",
                        typeName))
                .getColumn("t");
    }

}
