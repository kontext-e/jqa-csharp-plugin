package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import lombok.Getter;
import lombok.Setter;
import org.jqassistant.contrib.plugin.csharp.model.ArrayCreationDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.FieldDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InvokesDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberOwningTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ParameterDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.PropertyDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class MethodDescriptorImpl implements MethodDescriptor {

    private boolean isStatic;
    private boolean readonly;
    private boolean isSealed;
    private boolean isNew;
    private boolean isExtern;
    private boolean isOverride;
    private boolean isVirtual;
    private boolean isAbstract;
    private boolean isImplementation;
    private boolean isPartial;
    private boolean isExtensionMethod;

    private int cyclomaticComplexity;
    private int firstLineNumber;
    private int lastLineNumber;
    private int effectiveLineCount;

    private String name;
    private String fullQualifiedName;
    private String accessibility;
    private String signature;
    private String relativePath;
    private PropertyDescriptor associatedProperty;

    private List<ArrayCreationDescriptor> createsArray;
    private List<InvokesDescriptor> invokes;
    private List<InvokesDescriptor> invokedBy;
    private TypeDescriptor returns;
    private TypeDescriptor extendedType;
    private List<ParameterDescriptor> parameters = new ArrayList<>();
    private List<TypeDescriptor> declaredThrowables;
    private List<MethodDescriptor> methodFragments = new ArrayList<>();
    private List<FieldDescriptor> fields;
    private MemberOwningTypeDescriptor declaringType;

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
