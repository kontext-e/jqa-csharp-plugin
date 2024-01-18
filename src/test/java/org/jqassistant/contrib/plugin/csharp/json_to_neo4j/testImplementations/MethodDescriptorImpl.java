package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberAccessDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

public class MethodDescriptorImpl implements MethodDescriptor {

    private boolean isStatic;
    private boolean readonly;
    private boolean isConst;
    private boolean isSealed;
    private boolean isNew;
    private boolean isExtern;
    private boolean isOverride;
    private boolean isVirtual;
    private boolean isAbstract;
    private boolean abstractKeyword;
    private boolean isImplementation;
    private int cyclomaticComplexity;
    private List<InvokesDescriptor> invokes;
    private List<InvokesDescriptor> invokedBy;
    private String name;
    private String fullQualifiedName;
    private Integer lastLineNumber;
    private Integer firstLineNumber;
    private Integer effectiveLineCount;
    private TypeDescriptor returns;
    private List<ParameterDescriptor> parameters = new ArrayList<>();
    private List<TypeDescriptor> declaredThrowables;
    private List<MemberAccessDescriptor> accessedMember;
    private List<MethodDescriptor> methodFragments = new ArrayList<>();
    private List<FieldDescriptor> fields;
    private MemberOwningTypeDescriptor declaringType;
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
    public List<ParameterDescriptor> getParameters() {
        return parameters;
    }

    @Override
    public List<TypeDescriptor> getDeclaredThrowables() {
        return declaredThrowables;
    }

    public List<MemberAccessDescriptor> getAccessedMember() {
        return accessedMember;
    }

    @Override
    public List<MethodDescriptor> getMethodFragments() {
        return methodFragments;
    }

    @Override
    public List<FieldDescriptor> getFields() {
        return fields;
    }

    @Override
    public MemberOwningTypeDescriptor getDeclaringType() {
        return declaringType;
    }

    @Override
    public Boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(Boolean anAbstract) {
        isAbstract = anAbstract;
    }

    @Override
    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    @Override
    public void setCyclomaticComplexity(int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

    public boolean getIsImplementation() {
        return isImplementation;
    }

    public void setIsImplementation(boolean implementation) {
        isImplementation = implementation;
    }

    @Override
    public List<InvokesDescriptor> getInvokes() {
        return invokes;
    }

    public void setInvokes(List<InvokesDescriptor> invokes) {
        this.invokes = invokes;
    }

    @Override
    public List<InvokesDescriptor> getInvokedBy() {
        return invokedBy;
    }

    public void setInvokedBy(List<InvokesDescriptor> invokedBy) {
        this.invokedBy = invokedBy;
    }

    @Override
    public Integer getLastLineNumber() {
        return lastLineNumber;
    }

    @Override
    public void setLastLineNumber(Integer lastLineNumber) {
        this.lastLineNumber = lastLineNumber;
    }

    @Override
    public Integer getFirstLineNumber() {
        return firstLineNumber;
    }

    @Override
    public void setFirstLineNumber(Integer firstLineNumber) {
        this.firstLineNumber = firstLineNumber;
    }

    @Override
    public Integer getEffectiveLineCount() {
        return effectiveLineCount;
    }

    @Override
    public void setEffectiveLineCount(Integer effectiveLineCount) {
        this.effectiveLineCount = effectiveLineCount;
    }

    @Override
    public TypeDescriptor getReturns() {
        return returns;
    }

    @Override
    public void setReturns(TypeDescriptor returns) {
        this.returns = returns;
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
    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    @Override
    public void setFullQualifiedName(String fullQualifiedName) {
        this.fullQualifiedName = fullQualifiedName;
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

    public Boolean isAbstractKeyword() {
        return abstractKeyword;
    }

    public void setAbstractKeyword(Boolean abstractKeyword) {
        this.abstractKeyword = abstractKeyword;
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

    @Override
    public String getSignature() {
        return null;
    }

    @Override
    public void setSignature(String signature) {

    }
}
