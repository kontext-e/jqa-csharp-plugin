package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class ClassDescriptorImpl extends MemberOwningTypeDescriptorImpl implements ClassDescriptor {

    public ClassDescriptorImpl(String name) {
        super(name);
    }

    private boolean abstractKeyword;

    private TypeDescriptor superClass;
    private List<TypeDescriptor> interfaces = new ArrayList<>();

    private boolean isStatic;
    private boolean isSealed;
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

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean aStatic) {
        isStatic = aStatic;
    }

    public boolean isSealed() {
        return isSealed;
    }

    public void setSealed(boolean sealed) {
        this.isSealed = sealed;
    }

    public Boolean isAbstract() {
        return abstractKeyword;
    }

    public void setAbstract(Boolean abstractKeyword) {
        this.abstractKeyword = abstractKeyword;
    }

    public List<TypeDescriptor> getInterfaces() {
        return interfaces;
    }

}
