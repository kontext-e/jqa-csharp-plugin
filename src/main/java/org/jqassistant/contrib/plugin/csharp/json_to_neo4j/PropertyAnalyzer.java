package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.PropertyAccessorModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.PropertyModel;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PropertyAnalyzer {

    private final TypeCache typeCache;
    private final PropertyCache propertyCache;
    private final MethodAnalyzer methodAnalyzer;

    public PropertyAnalyzer(TypeCache typeCache, PropertyCache propertyCache, MethodAnalyzer methodAnalyzer) {
        this.typeCache = typeCache;
        this.propertyCache = propertyCache;
        this.methodAnalyzer = methodAnalyzer;
    }

    public void createProperties(List<FileModel> fileModelList) {
        for (FileModel fileModel : fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {
                ClassDescriptor classDescriptor = (ClassDescriptor) typeCache.findAny(classModel.getKey());

                for (PropertyModel propertyModel : classModel.getProperties()) {
                    PropertyDescriptor propertyDescriptor = propertyCache.create(propertyModel.getKey());
                    fillPropertyDescriptor(propertyModel, propertyDescriptor);
                    classDescriptor.getDeclaredMembers().add(propertyDescriptor);
                }
            }
        }
    }

    private void fillPropertyDescriptor(PropertyModel propertyModel, PropertyDescriptor propertyDescriptor) {
        propertyDescriptor.setFullQualifiedName(propertyModel.getFqn());
        propertyDescriptor.setName(propertyModel.getName());
        propertyDescriptor.setAccessibility(propertyModel.getAccessibility());
        propertyDescriptor.setStatic(propertyModel.isStaticKeyword());
        propertyDescriptor.setRequired(propertyModel.isRequired());

        TypeDescriptor typeDescriptor = typeCache.findOrCreate(propertyModel.getType());
        propertyDescriptor.setType(typeDescriptor);

        List<MethodDescriptor> accessors = findAndCreateAccessors(propertyModel);
        propertyDescriptor.getAccessors().addAll(accessors);
    }

    private List<MethodDescriptor> findAndCreateAccessors(PropertyModel propertyModel) {
        List<MethodDescriptor> accessors = new ArrayList<>();

        Optional<PropertyAccessorModel> getter = propertyModel.getAccessors().stream().filter(t -> t.getKind().contains("get")).findAny();
        getter.ifPresent(accessor -> accessors.add(createAccessors(propertyModel, accessor)));

        Optional<PropertyAccessorModel> setter = propertyModel.getAccessors().stream().filter(t -> t.getKind().contains("set")).findAny();
        setter.ifPresent(accessor -> accessors.add(createAccessors(propertyModel, accessor)));

        Optional<PropertyAccessorModel> init = propertyModel.getAccessors().stream().filter(t -> t.getKind().contains("init")).findAny();
        init.ifPresent(accessor -> accessors.add(createAccessors(propertyModel, accessor)));

        return accessors;
    }

    private MethodDescriptor createAccessors(PropertyModel propertyModel, PropertyAccessorModel accessor) {
        MethodModel methodModel = new MethodModel();
        methodModel.setName(accessor.getKind() + propertyModel.getName());
        methodModel.setFqn(propertyModel.getFqn() + "." + accessor.getKind());
        methodModel.setParameters(new ArrayList<>());
        methodModel.setAccessibility(accessor.getAccessibility());

        return methodAnalyzer.createMethodDescriptor(methodModel);
    }

}
