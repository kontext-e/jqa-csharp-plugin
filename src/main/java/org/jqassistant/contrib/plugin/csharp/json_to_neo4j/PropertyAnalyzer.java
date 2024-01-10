package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.apache.commons.lang.StringUtils;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
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

        TypeDescriptor typeDescriptor = typeCache.findOrCreate(propertyModel.getType());
        propertyDescriptor.setType(typeDescriptor);

        List<MethodDescriptor> accessors = findAndCreateAccessors(propertyModel);
        propertyDescriptor.getAccessors().addAll(accessors);
    }

    private List<MethodDescriptor> findAndCreateAccessors(PropertyModel propertyModel) {
        List<MethodDescriptor> accessors = new ArrayList<>();

        Optional<String> getter = propertyModel.getAccessors().stream().filter(t -> t.contains("get")).findAny();
        getter.ifPresent(s -> accessors.add(createAccessors(propertyModel, s.trim())));

        Optional<String> setter = propertyModel.getAccessors().stream().filter(t -> t.contains("set")).findAny();
        setter.ifPresent(s -> accessors.add(createAccessors(propertyModel, s.trim())));

        Optional<String> init = propertyModel.getAccessors().stream().filter(t -> t.contains("init")).findAny();
        init.ifPresent(s -> accessors.add(createAccessors(propertyModel, s.trim())));

        return accessors;
    }

    //TODO Rework
    private MethodDescriptor createAccessors(PropertyModel propertyModel, String accessor) {
        String kindOfAccessor = accessor.contains(" ") ?
                accessor.substring(accessor.lastIndexOf(" ")).trim() : accessor;

        MethodModel methodModel = new MethodModel();
        methodModel.setName(kindOfAccessor + propertyModel.getName());
        methodModel.setFqn(propertyModel.getFqn() + "." + kindOfAccessor);
        methodModel.setParameters(new ArrayList<>());

        if (accessor.contains(" ")) {
            methodModel.setAccessibility(StringUtils.capitalize(StringUtils.substringBeforeLast(accessor, " ").trim()));
        } else {
            methodModel.setAccessibility(propertyModel.getAccessibility());
        }
        return methodAnalyzer.createMethodDescriptor(methodModel);
    }

}
