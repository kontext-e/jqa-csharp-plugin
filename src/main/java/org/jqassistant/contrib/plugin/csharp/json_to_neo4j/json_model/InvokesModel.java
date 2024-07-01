package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Setter
@Getter
public class InvokesModel {

    private int lineNumber;

    private String methodId;

    private List<String> typeArguments;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvokesModel that = (InvokesModel) o;
        return lineNumber == that.lineNumber && Objects.equals(methodId, that.methodId) && Objects.equals(typeArguments, that.typeArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lineNumber, methodId, typeArguments);
    }
}
