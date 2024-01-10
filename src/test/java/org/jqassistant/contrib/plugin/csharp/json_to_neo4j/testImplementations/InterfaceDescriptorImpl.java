package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class InterfaceDescriptorImpl extends TypeDescriptorImpl implements InterfaceTypeDescriptor {
    private String visibility;
    private boolean isStatic;
    private boolean readonly;
    private boolean isConst;
    private boolean isSealed;
    private boolean isNew;
    private boolean isExtern;
    private boolean isOverride;
    private boolean isVirtual;
    private List<TypeDescriptor> interfaces = new ArrayList<>();

    public InterfaceDescriptorImpl(String name) {
        super(name);
    }


    @Override
    public Boolean isStatic() {
        return isStatic;
    }

    public void setStatic(Boolean aStatic) {
        isStatic = aStatic;
    }

    @Override
    public Boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    @Override
    public Boolean isConst() {
        return isConst;
    }

    public void setConst(Boolean aConst) {
        isConst = aConst;
    }

    @Override
    public Boolean isSealed() {
        return isSealed;
    }

    public void setSealed(Boolean sealed) {
        isSealed = sealed;
    }

    @Override
    public Boolean isNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    @Override
    public Boolean isExtern() {
        return isExtern;
    }

    public void setExtern(Boolean extern) {
        isExtern = extern;
    }

    @Override
    public Boolean isOverride() {
        return isOverride;
    }

    public void setOverride(Boolean override) {
        isOverride = override;
    }

    @Override
    public Boolean isVirtual() {
        return isVirtual;
    }

    public void setVirtual(Boolean virtual) {
        isVirtual = virtual;
    }

    @Override
    public List<TypeDescriptor> getInterfaces() {
        return interfaces;
    }
}
