package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.apache.commons.lang.StringUtils;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.FieldCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.PropertyCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.*;
import org.jqassistant.contrib.plugin.csharp.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MemberAnalyzer {
    private final JsonToNeo4JConverter jsonToNeo4JConverter;
    private final Store store;

    private final FieldCache fieldCache;
    private final PropertyCache propertyCache;
    private final TypeCache typeCache;

    public MemberAnalyzer(JsonToNeo4JConverter jsonToNeo4JConverter, Store store, FieldCache fieldCache,
                          PropertyCache propertyCache, TypeCache typeCache) {
        this.jsonToNeo4JConverter = jsonToNeo4JConverter;
        this.store = store;
        this.fieldCache = fieldCache;
        this.propertyCache = propertyCache;
        this.typeCache = typeCache;
    }

    protected void createFields() {

        for (FileModel fileModel : jsonToNeo4JConverter.fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {

                Optional<TypeDescriptor> descriptor = typeCache.findTypeByRelativePath(classModel.getKey(), fileModel.getRelativePath());
                if (!descriptor.isPresent()) continue;

                ClassDescriptor classDescriptor = (ClassDescriptor) descriptor.get();
                for (FieldModel fieldModel : classModel.getFields()) {

                    FieldDescriptor fieldDescriptor = fieldCache.create(fieldModel.getKey());
                    fieldDescriptor.setFullQualifiedName(fieldModel.getFqn());
                    fieldDescriptor.setName(fieldModel.getName());
                    fieldDescriptor.setVisibility(fieldModel.getAccessibility());

                    TypeDescriptor typeDescriptor = typeCache.findOrCreate(fieldModel.getType());
                    fieldDescriptor.setType(typeDescriptor);

                    fieldDescriptor.setVolatile(fieldModel.isVolatileKeyword());
                    fieldDescriptor.setSealed(fieldModel.isSealed());
                    fieldDescriptor.setStatic(fieldModel.isStaticKeyword());

                    if (StringUtils.isNotBlank(fieldModel.getConstantValue())) {
                        PrimitiveValueDescriptor primitiveValueDescriptor = store.create(PrimitiveValueDescriptor.class);
                        primitiveValueDescriptor.setValue(fieldModel.getConstantValue());
                        fieldDescriptor.setValue(primitiveValueDescriptor);
                    }

                    classDescriptor.getDeclaredMembers().add(fieldDescriptor);
                }
            }
        }
    }

    protected void createProperties() {
        for (FileModel fileModel : jsonToNeo4JConverter.fileModelList) {
            for (ClassModel classModel : fileModel.getClasses()) {
                ClassDescriptor classDescriptor = (ClassDescriptor) typeCache.findAny(classModel.getKey());

                for (PropertyModel propertyModel : classModel.getProperties()) {
                    PropertyDescriptor propertyDescriptor = propertyCache.create(propertyModel.getKey());
                    propertyDescriptor.setFullQualifiedName(propertyModel.getFqn());
                    propertyDescriptor.setName(propertyModel.getName());
                    propertyDescriptor.setVisibility(propertyModel.getAccessibility());
                    propertyDescriptor.setStatic(propertyModel.isStaticKeyword());

                    TypeDescriptor typeDescriptor = typeCache.findOrCreate(propertyModel.getType());
                    propertyDescriptor.setType(typeDescriptor);

                    List<MethodDescriptor> accessors = findAndCreateAccessors(propertyModel);
                    propertyDescriptor.getAccessors().addAll(accessors);

                    classDescriptor.getDeclaredMembers().add(propertyDescriptor);
                }
            }
        }
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
        return jsonToNeo4JConverter.methodAnalyzer.createMethodDescriptor(methodModel);
    }
}