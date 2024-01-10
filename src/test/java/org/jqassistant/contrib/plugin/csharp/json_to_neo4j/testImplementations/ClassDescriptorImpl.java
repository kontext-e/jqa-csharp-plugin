package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ClassDescriptorImpl extends TypeDescriptorImpl implements ClassDescriptor {

    public ClassDescriptorImpl(String name) {
        super(name);
    }

    private boolean abstractKeyword;
    private boolean partial;

    private TypeDescriptor superClass;
    private List<TypeDescriptor> interfaces = new ArrayList<>();

    private String visibility;
    private boolean isStatic;
    private boolean readonly;
    private boolean isConst;
    private boolean isSealed;
    private boolean isNew;
    private boolean isExtern;
    private boolean isOverride;
    private boolean isVirtual;
    private String accessibility;

    @Override
    public String getAccessibility() {
        return accessibility;
    }

    @Override
    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }


    @Override
    public TypeDescriptor getSuperClass() {
        return superClass;
    }

    @Override
    public void setSuperClass(TypeDescriptor superClass) {
        this.superClass = superClass;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Boolean isStatic() {
        return isStatic;
    }

    public void setStatic(Boolean aStatic) {
        isStatic = aStatic;
    }

    public Boolean isReadonly() {
        return readonly;
    }

    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    public Boolean isConst() {
        return isConst;
    }

    public void setConst(Boolean aConst) {
        isConst = aConst;
    }

    public Boolean isSealed() {
        return isSealed;
    }

    public void setSealed(Boolean sealed) {
        this.isSealed = sealed;
    }

    public Boolean isNew() {
        return isNew;
    }

    public void setNew(Boolean aNew) {
        isNew = aNew;
    }

    public Boolean isExtern() {
        return isExtern;
    }

    public void setExtern(Boolean extern) {
        isExtern = extern;
    }

    public Boolean isOverride() {
        return isOverride;
    }

    public void setOverride(Boolean override) {
        isOverride = override;
    }

    public Boolean isVirtual() {
        return isVirtual;
    }

    public void setVirtual(Boolean virtual) {
        isVirtual = virtual;
    }

    public Boolean isAbstract() {
        return abstractKeyword;
    }

    public void setAbstract(Boolean abstractKeyword) {
        this.abstractKeyword = abstractKeyword;
    }


    public Boolean isPartial() {
        return partial;
    }

    public void setPartial(boolean partial) {
        this.partial = partial;
    }

    public List<TypeDescriptor> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<TypeDescriptor> interfaces) {
        this.interfaces = interfaces;
    }
}
