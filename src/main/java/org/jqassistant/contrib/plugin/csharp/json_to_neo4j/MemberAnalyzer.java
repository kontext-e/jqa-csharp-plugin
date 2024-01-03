package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.apache.commons.lang.StringUtils;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.FieldCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FieldModel;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PrimitiveValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.Optional;

public class MemberAnalyzer {
    private final Store store;

    private final FieldCache fieldCache;
    private final TypeCache typeCache;

    public MemberAnalyzer(Store store, FieldCache fieldCache, TypeCache typeCache) {
        this.store = store;
        this.fieldCache = fieldCache;
        this.typeCache = typeCache;
    }

    protected void createFields(ClassModel classModel, String relativeFilePath) {

        Optional<TypeDescriptor> descriptor = typeCache.findTypeByRelativePath(classModel.getKey(), relativeFilePath);
        if (!descriptor.isPresent()) return;

        ClassDescriptor classDescriptor = (ClassDescriptor) descriptor.get();
        for (FieldModel fieldModel : classModel.getFields()) {
            FieldDescriptor fieldDescriptor = fieldCache.create(fieldModel.getKey());
            fillFieldDescriptor(fieldModel, fieldDescriptor);
            classDescriptor.getDeclaredMembers().add(fieldDescriptor);
        }
    }

    private void fillFieldDescriptor(FieldModel fieldModel, FieldDescriptor fieldDescriptor) {
        analyzeFieldProperties(fieldModel, fieldDescriptor);
        analyzeFieldType(fieldModel, fieldDescriptor);
        analyzeConstantValues(fieldModel, fieldDescriptor);
    }

    private void analyzeFieldProperties(FieldModel fieldModel, FieldDescriptor fieldDescriptor) {
        fieldDescriptor.setFullQualifiedName(fieldModel.getFqn());
        fieldDescriptor.setName(fieldModel.getName());
        fieldDescriptor.setVisibility(fieldModel.getAccessibility());
        fieldDescriptor.setVolatile(fieldModel.isVolatileKeyword());
        fieldDescriptor.setSealed(fieldModel.isSealed());
        fieldDescriptor.setStatic(fieldModel.isStaticKeyword());
    }

    private void analyzeFieldType(FieldModel fieldModel, FieldDescriptor fieldDescriptor) {
        TypeDescriptor typeDescriptor = typeCache.findOrCreate(fieldModel.getType());
        fieldDescriptor.setType(typeDescriptor);
    }

    private void analyzeConstantValues(FieldModel fieldModel, FieldDescriptor fieldDescriptor) {
        if (StringUtils.isNotBlank(fieldModel.getConstantValue())) {
            PrimitiveValueDescriptor primitiveValueDescriptor = store.create(PrimitiveValueDescriptor.class);
            primitiveValueDescriptor.setValue(fieldModel.getConstantValue());
            fieldDescriptor.setValue(primitiveValueDescriptor);
        }
    }
}