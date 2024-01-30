package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.LinkedList;
import java.util.List;

public class PartialityAnalyzer {

    private final MethodCache methodCache;
    private final TypeCache typeCache;

    public PartialityAnalyzer(MethodCache methodCache, TypeCache typeCache) {
        this.methodCache = methodCache;
        this.typeCache = typeCache;
    }

    public void linkPartialMethods(){
        List<List<MethodDescriptor>> methodDescriptors = methodCache.findAllPartialMethods();
        for (List<MethodDescriptor> methodFragments : methodDescriptors){
            for (MethodDescriptor methodDescriptor : methodFragments){
                if (!methodDescriptor.isImplementation()) continue;
                addSiblings(methodFragments, methodDescriptor);
            }
        }
    }

    public void linkPartialClasses() {
        List<List<TypeDescriptor>> partialTypes = typeCache.findAllPartialTypes();
        for (List<TypeDescriptor> typeFragments : partialTypes){
            for (TypeDescriptor typeFragment : typeFragments){
                //TODO reuse addSiblings(...)
                List<TypeDescriptor> siblings = new LinkedList<>(typeFragments);
                siblings.remove(typeFragment);
                typeFragment.getTypeFragments().addAll(siblings);
            }
        }
    }

    private static void addSiblings(List<MethodDescriptor> methodFragments, MethodDescriptor methodDescriptor) {
        List<MethodDescriptor> siblings = new LinkedList<>(methodFragments);
        siblings.remove(methodDescriptor);
        methodDescriptor.getMethodFragments().addAll(siblings);
    }
}
