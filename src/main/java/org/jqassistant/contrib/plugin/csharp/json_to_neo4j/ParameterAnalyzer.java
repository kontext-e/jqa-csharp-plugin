package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ParameterModel;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ParameterAnalyzer {


    private final TypeCache typeCache;
    private final Store store;

    protected ParameterAnalyzer(TypeCache typeCache, Store store) {
        this.typeCache = typeCache;
        this.store = store;
    }

    protected void addParameters(MethodModel methodModel, MethodDescriptor methodDescriptor) {
        List<ParameterDescriptor> parameterDescriptors = createParameterDescriptors(methodModel);
        methodDescriptor.getParameters().addAll(parameterDescriptors);
    }

    private List<ParameterDescriptor> createParameterDescriptors(MethodModel methodModel) {

        List<ParameterDescriptor> parameterDescriptors = new ArrayList<>();
        for (int index = 0; index < methodModel.getParameters().size(); index++){
            createParameterDescriptor(methodModel, parameterDescriptors, index);
        }
        return parameterDescriptors;
    }

    private void createParameterDescriptor(MethodModel methodModel, List<ParameterDescriptor> parameterDescriptors, int index) {

        ParameterModel parameterModel = methodModel.getParameters().get(index);
        ParameterDescriptor parameterDescriptor = store.create(ParameterDescriptor.class);
        fillParameterDescriptor(index, parameterModel, parameterDescriptor);
        parameterDescriptors.add(parameterDescriptor);
    }

    private void fillParameterDescriptor(int index, ParameterModel parameterModel, ParameterDescriptor parameterDescriptor) {

        parameterDescriptor.setName(parameterModel.getName());
        parameterDescriptor.setIndex(index);
        for (String type : parameterModel.getTypes()) {
            TypeDescriptor parameterTypeDescriptor = typeCache.findOrCreate(type);
            parameterDescriptor.getTypes().add(parameterTypeDescriptor);
        }
    }
}
