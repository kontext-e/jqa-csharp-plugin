package org.jqassistant.contrib.plugin.csharp.model;

public interface LineCountDescriptor {

    Integer getFirstLineNumber();
    void setFirstLineNumber(Integer firstLineNumber);

    Integer getLastLineNumber();
    void setLastLineNumber(Integer lastLineNumber);

    Integer getEffectiveLineCount();
    void setEffectiveLineCount(Integer effectiveLineCount);

}
