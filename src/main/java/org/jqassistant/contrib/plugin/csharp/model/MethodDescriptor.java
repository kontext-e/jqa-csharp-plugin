package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Incoming;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;


@SuppressWarnings("unused")
@Label(value = "Method")
public interface MethodDescriptor extends MemberDescriptor, AbstractDescriptor, PartialDescriptor {


    @Relation("HAS")
    List<ParameterDescriptor> getParameters();

    @Relation("RETURNS")
    List<TypeDescriptor> getReturns();
    void setReturns(List<TypeDescriptor> returns);

    @Relation("THROWS")
    List<TypeDescriptor> getDeclaredThrowables();

    @Outgoing
    @Relation("INVOKES")
    List<InvokesDescriptor> getInvokes();

    @Outgoing
    List<ArrayCreationDescriptor> getCreatesArray();

    @Relation("PARTIAL_WITH")
    List<MethodDescriptor> getMethodFragments();

    @Declares
    List<FieldDescriptor> getFields();

    @Incoming
    PropertyDescriptor getAssociatedProperty();
    void setAssociatedProperty(PropertyDescriptor propertyDescriptor);

    @Relation("EXTENDS")
    TypeDescriptor getExtendedType();
    void setExtendedType(TypeDescriptor typeDescriptor);

    boolean isImplementation();
    void setImplementation(boolean isImplementation);

    int getCyclomaticComplexity();
    void setCyclomaticComplexity(int cyclomaticComplexity);

    int getFirstLineNumber();
    void setFirstLineNumber(int firstLineNumber);

    int getLastLineNumber();
    void setLastLineNumber(int lastLineNumber);

    int getEffectiveLineCount();
    void setEffectiveLineCount(int effectiveLineCount);

    boolean isStatic();
    void setStatic(boolean s);

    boolean isReadonly();
    void setReadonly(boolean r);

    boolean isSealed();
    void setSealed(boolean s);

    boolean isNew();
    void setNew(boolean n);

    boolean isExtern();
    void setExtern(boolean e);

    boolean isOverride();
    void setOverride(boolean o);

    boolean isVirtual();
    void setVirtual(boolean v);

    boolean isExtensionMethod();
    void setExtensionMethod(boolean e);
}
