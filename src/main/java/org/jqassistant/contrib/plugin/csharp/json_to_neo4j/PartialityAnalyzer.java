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
                if (!methodDescriptor.getIsImplementation()) continue;
                addSiblings(methodFragments, methodDescriptor);
            }
        }
    }

    public void linkPartialClasses() {
        List<List<TypeDescriptor>> partialClasses = typeCache.findAllPartialClasses();
        for (List<TypeDescriptor> classFragments : partialClasses){
            for (TypeDescriptor classFragment : classFragments){
                //TODO reuse addSiblings(...)
                List<TypeDescriptor> siblings = new LinkedList<>(classFragments);
                siblings.remove(classFragment);
                classFragment.getClassFragments().addAll(siblings);
            }
        }
    }

    private static void addSiblings(List<MethodDescriptor> methodFragments, MethodDescriptor methodDescriptor) {
        List<MethodDescriptor> siblings = new LinkedList<>(methodFragments);
        siblings.remove(methodDescriptor);
        methodDescriptor.getMethodFragments().addAll(siblings);
    }
}
