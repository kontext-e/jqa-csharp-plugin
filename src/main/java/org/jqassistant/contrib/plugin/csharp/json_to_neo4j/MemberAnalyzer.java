package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.apache.commons.lang.StringUtils;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.FieldCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FieldModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PrimitiveValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.List;
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

    protected void createFields(List<FileModel> fileModelList) {

        for (FileModel fileModel : fileModelList) {
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
}