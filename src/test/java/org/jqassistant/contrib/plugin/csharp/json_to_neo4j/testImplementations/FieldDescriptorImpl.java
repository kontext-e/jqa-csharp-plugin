package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PrimitiveValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

public class FieldDescriptorImpl implements FieldDescriptor {

    private String visibility;
    private boolean isStatic;
    private boolean readonly;
    private boolean isConst;
    private boolean isSealed;
    private boolean isNew;
    private boolean isExtern;
    private boolean isOverride;
    private boolean isVirtual;


    private String signature;
    private Boolean isTransient;
    private Boolean isVolatile;
    private String FullQualifiedName;
    private String name;
    private TypeDescriptor type;
    private PrimitiveValueDescriptor value;
    private MemberOwningDescriptor declaringType;
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
    public MemberOwningDescriptor getDeclaringType() {
        return declaringType;
    }

    @Override
    public PrimitiveValueDescriptor getValue() {
        return value;
    }

    @Override
    public void setValue(PrimitiveValueDescriptor value) {
        this.value = value;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Boolean isTransient() {
        return isTransient;
    }

    @Override
    public void setTransient(Boolean aTransient) {
        isTransient = aTransient;
    }

    public Boolean isVolatile() {
        return isVolatile;
    }

    @Override
    public void setVolatile(Boolean aVolatile) {
        isVolatile = aVolatile;
    }

    public String getFullQualifiedName() {
        return FullQualifiedName;
    }

    public void setFullQualifiedName(String fullQualifiedName) {
        FullQualifiedName = fullQualifiedName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public TypeDescriptor getType() {
        return type;
    }

    @Override
    public void setType(TypeDescriptor type) {
        this.type = type;
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
    public <I> I getId() {
        return null;
    }

    @Override
    public <T> T as(Class<T> aClass) {
        return null;
    }

    @Override
    public <D> D getDelegate() {
        return null;
    }
}
