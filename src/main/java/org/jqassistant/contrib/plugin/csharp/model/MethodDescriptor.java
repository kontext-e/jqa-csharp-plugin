package org.jqassistant.contrib.plugin.csharp.model;

import com.buschmais.xo.neo4j.api.annotation.Label;
import com.buschmais.xo.neo4j.api.annotation.Relation;
import com.buschmais.xo.neo4j.api.annotation.Relation.Outgoing;

import java.util.List;


@SuppressWarnings("unused")
@Label(value = "Method")
public interface MethodDescriptor extends MemberDescriptor, AbstractDescriptor {


    @Relation("HAS")
    List<ParameterDescriptor> getParameters();

    @Relation("RETURNS")
    TypeDescriptor getReturns();
    void setReturns(TypeDescriptor returns);

    @Relation("THROWS")
    List<TypeDescriptor> getDeclaredThrowables();

    @Outgoing
    List<InvokesDescriptor> getInvokes();

    @Relation.Incoming
    List<InvokesDescriptor> getInvokedBy();

    @Outgoing
    List<MemberAccessDescriptor> getAccessedMember();

    @Relation("PARTIAL_WITH")
    List<MethodDescriptor> getMethodFragments();

    @Declares
    List<FieldDescriptor> getFields();

    @Relation("EXTENDS")
    TypeDescriptor getExtendedType();
    void setExtendedType(TypeDescriptor typeDescriptor);

    boolean getIsImplementation();
    void setIsImplementation(boolean isImplementation);

    int getCyclomaticComplexity();
    void setCyclomaticComplexity(int cyclomaticComplexity);

    Integer getFirstLineNumber();
    void setFirstLineNumber(Integer firstLineNumber);

    Integer getLastLineNumber();
    void setLastLineNumber(Integer lastLineNumber);

    Integer getEffectiveLineCount();
    void setEffectiveLineCount(Integer effectiveLineCount);

    Boolean isStatic();
    void setStatic(Boolean s);

    Boolean isReadonly();
    void setReadonly(Boolean r);

    Boolean isConst();
    void setConst(Boolean c);

    Boolean isSealed();
    void setSealed(Boolean s);

    Boolean isNew();
    void setNew(Boolean n);

    Boolean isExtern();
    void setExtern(Boolean e);

    Boolean isOverride();
    void setOverride(Boolean o);

    Boolean isVirtual();
    void setVirtual(Boolean v);
}
