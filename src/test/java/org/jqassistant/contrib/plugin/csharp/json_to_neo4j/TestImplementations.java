package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberAccessDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PrimitiveValueDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class TypeDescriptorImpl implements TypeDescriptor {

    TypeDescriptorImpl(String name){
        this.name = name;
    }

    private List<TypeDescriptor> classFragments = new LinkedList<>();
    private String name;
    private List<MemberDescriptor> declaredMembers = new LinkedList<>();
    private Integer lastLineNumber;
    private Integer firstLineNumber;
    private Integer effectiveLineCount;
    private String fullQualifiedName;
    private String Md5;
    private String relativePath;
    private boolean partial;

    public List<MemberDescriptor> getDeclaredMembers() {
        return declaredMembers;
    }

    public Integer getFirstLineNumber() {
        return firstLineNumber;
    }

    public void setFirstLineNumber(Integer firstLineNumber) {
        this.firstLineNumber = firstLineNumber;
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
    public List<TypeDescriptor> getClassFragments() {
        return classFragments;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        this.name = s;
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
    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    @Override
    public void setFullQualifiedName(String fullQualifiedName) {
        this.fullQualifiedName = fullQualifiedName;
    }

    @Override
    public String getMd5() {
        return Md5;
    }

    @Override
    public void setMd5(String md5) {
        Md5 = md5;
    }

    @Override
    public String getRelativePath() {
        return relativePath;
    }

    @Override
    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    @Override
    public boolean getPartial() {
        return partial;
    }

    @Override
    public void setPartial(boolean partial) {
        this.partial = partial;
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

class ClassDescriptorImpl extends TypeDescriptorImpl implements ClassDescriptor {

    ClassDescriptorImpl(String name) {
        super(name);
    }

    private boolean abstractKeyword;
    private boolean partial;

    private TypeDescriptor superClass;
    private List<TypeDescriptor> interfaces;

    private String visibility;
    private boolean isStatic;
    private boolean readonly;
    private boolean isConst;
    private boolean isSealed;
    private boolean isNew;
    private boolean isExtern;
    private boolean isOverride;
    private boolean isVirtual;


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

class InterfaceDescriptorImpl extends TypeDescriptorImpl implements InterfaceTypeDescriptor {
    private String visibility;
    private boolean isStatic;
    private boolean readonly;
    private boolean isConst;
    private boolean isSealed;
    private boolean isNew;
    private boolean isExtern;
    private boolean isOverride;
    private boolean isVirtual;
    private List<TypeDescriptor> interfaces;

    InterfaceDescriptorImpl(String name) {
        super(name);
    }

    @Override
    public String getVisibility() {
        return visibility;
    }

    @Override
    public void setVisibility(String visibility) {
        this.visibility = visibility;
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

class EnumDescriptorImpl extends ClassDescriptorImpl implements EnumTypeDescriptor {
    EnumDescriptorImpl(String name) {
        super(name);
    }
}

class EnumValueDescriptorIml implements EnumValueDescriptor {

    private String name;
    private TypeDescriptor type;
    private FieldDescriptor value;

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

    public FieldDescriptor getValue() {
        return value;
    }

    public void setValue(FieldDescriptor value) {
        this.value = value;
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

class NamespaceDescriptorImpl implements NamespaceDescriptor {

    private String fullQualifiedName;
    private List<UsesNamespaceDescriptor> usedBy = new LinkedList<>();
    private List<TypeDescriptor> contains = new LinkedList<>();

    public NamespaceDescriptorImpl() {
    }

    @Override
    public List<UsesNamespaceDescriptor> getUsedBy() {
        return usedBy;
    }

    @Override
    public List<TypeDescriptor> getContains() {
        return contains;
    }

    @Override
    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    @Override
    public void setFullQualifiedName(String s) {
        fullQualifiedName = s;
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

class CSharpFileDescriptorImpl implements CSharpFileDescriptor {

    private String name;
    private String fileName;
    private final Set<FileDescriptor> parents = new LinkedHashSet<>();
    private final List<UsesNamespaceDescriptor> uses = new LinkedList<>();
    private final List<TypeDescriptor> types = new LinkedList<>();

    public CSharpFileDescriptorImpl(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
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
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public Set<FileDescriptor> getParents() {
        return parents;
    }


    @Override
    public List<UsesNamespaceDescriptor> getUses() {
        return uses;
    }

    @Override
    public List<TypeDescriptor> getTypes() {
        return types;
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

class MethodDescriptorImpl implements MethodDescriptor {

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
    private String visibility;
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
    private TypeDescriptor declaringType;

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
    public TypeDescriptor getDeclaringType() {
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
    public String getVisibility() {
        return visibility;
    }

    @Override
    public void setVisibility(String visibility) {
        this.visibility = visibility;
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

class ConstructorDescriptorImpl extends MethodDescriptorImpl implements ConstructorDescriptor {}

class PrimitiveValueDescriptorImpl implements PrimitiveValueDescriptor{

    private String name;
    private TypeDescriptor type;
    private Object value;

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
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
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

class FieldDescriptorImpl implements FieldDescriptor{

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
    private TypeDescriptor declaringType;

    public TypeDescriptor getDeclaringType() {
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
    public String getVisibility() {
        return visibility;
    }

    @Override
    public void setVisibility(String visibility) {
        this.visibility = visibility;
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
