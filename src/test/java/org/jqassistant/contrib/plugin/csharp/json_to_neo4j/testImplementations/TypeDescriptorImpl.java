package org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations;

import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;

import java.util.LinkedList;
import java.util.List;

public class TypeDescriptorImpl implements TypeDescriptor {

    public TypeDescriptorImpl(String name){
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

