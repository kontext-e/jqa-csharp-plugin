package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class MethodModel implements JsonModel {

    private String name;

    private String fqn;

    @JsonProperty("static")
    private boolean staticKeyword;

    @JsonProperty("abstract")
    private boolean abstractKeyword;

    private boolean sealed;

    private boolean async;

    private boolean override;

    private boolean virtual;

    private String accessibility;

    private String returnType;

    private String associatedProperty;

    private int firstLineNumber;

    private int lastLineNumber;

    private int effectiveLineCount;

    private int cyclomaticComplexity;

    @JsonProperty("isImplementation")
    private boolean isImplementation;

    private boolean partial;

    @JsonProperty("isExtensionMethod")
    private boolean isExtensionMethod;

    private String extendsType;

    private List<InvokesModel> invokedBy;

    private List<ParameterModel> parameters;

    @Override
    public String getKey() {
        return fqn;
    }
}
