package org.jqassistant.contrib.plugin.csharp.model;

public interface PartialDescriptor {

    //Used to uniquely identify Objects with the
    //same name, i.e. Partial Methods/Classes etc.
    String getRelativePath();

    void setRelativePath(String path);

    boolean isPartial();

    void setPartial(boolean partial);

}
