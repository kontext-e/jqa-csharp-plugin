package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class FileModel {

    private String name;

    private String absolutePath;

    private String relativePath;

    private List<ClassModel> classes = new ArrayList<>();

    private List<UsingModel> usings = new ArrayList<>();

    private List<EnumModel> enums = new ArrayList<>();

    private List<InterfaceModel> interfaces = new ArrayList<>();

    private List<StructModel> structs = new ArrayList<>();

    private List<RecordClassModel> recordClasses = new ArrayList<>();

    private List<RecordStructModel> recordStructs = new ArrayList<>();

    public List<MemberOwningTypeModel> getMemberOwningTypes(){
        ArrayList<MemberOwningTypeModel> memberOwningTypeModels = new ArrayList<>();
        memberOwningTypeModels.addAll(classes);
        memberOwningTypeModels.addAll(interfaces);
        memberOwningTypeModels.addAll(structs);
        memberOwningTypeModels.addAll(recordClasses);
        memberOwningTypeModels.addAll(recordStructs);
        return memberOwningTypeModels;
    }

    public List<TypeModel> getTypeModels(){
        List<TypeModel> typeModels = new ArrayList<>(getMemberOwningTypes());
        typeModels.addAll(enums);
        return typeModels;
    }

}
