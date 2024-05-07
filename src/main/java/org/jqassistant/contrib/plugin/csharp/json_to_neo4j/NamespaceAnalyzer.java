package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;

import java.util.List;

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
}
