package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.model.FullQualifiedNameDescriptor;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NamespaceAnalyzer {

    private final NamespaceCache namespaceCache;

    public NamespaceAnalyzer(NamespaceCache namespaceCache) {
        this.namespaceCache = namespaceCache;
    }

    public void addContainsRelationToNamespaces(){
        List<NamespaceDescriptor> namespaces = namespaceCache.getAllNamespaces();
        for (NamespaceDescriptor containingNamespace : namespaces) {
            for (NamespaceDescriptor containedNamespace : namespaces) {
                if (NamespaceContainsOther(containingNamespace, containedNamespace)) {
                    containingNamespace.getContainingNameSpaces().add(containedNamespace);
                }
            }
        }
    }

    private boolean NamespaceContainsOther(NamespaceDescriptor containingNamespace, NamespaceDescriptor containedNamespace) {
        String[] containingNamespaceParts = containingNamespace.getFullQualifiedName().split("\\.");
        String[] containedNamespaceParts = containedNamespace.getFullQualifiedName().split("\\.");
        if (containingNamespaceParts.length >= containedNamespaceParts.length) return false;

        for (int i = 0; i < containingNamespaceParts.length; i++) {
            if (!containingNamespaceParts[i].equals(containedNamespaceParts[i])) {
                return false;
            }
        }

        return true;
    }

    public void constructMissingHigherLevelNamespaces(){
        List<String> namespaces = namespaceCache.getAllNamespaceFQNs();
        for (String namespace : namespaces){
            createHigherLevelNamespaces(namespace, namespaces);
        }
    }

    private void createHigherLevelNamespaces(String namespace, List<String> namespaces) {
        List<String> namespaceParts = Arrays.asList(namespace.split("\\."));
        for (int i = 0; i < namespaceParts.size(); i++){
            String higherLevelNamespace = namespaceParts.stream().limit(i).collect(Collectors.joining("."));
            if (!namespaces.contains(higherLevelNamespace)) {
                namespaceCache.findOrCreate(higherLevelNamespace);
            }
        }
    }
}
