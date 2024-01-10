package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.MethodCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ConstructorModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.MethodModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ClassDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ConstructorDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.InterfaceDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.MethodDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.ConstructorDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MethodDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MethodAnalyzerTest {

    Store store;
    MethodCache methodCache;
    TypeCache typeCache;
    MethodAnalyzer methodAnalyzer;

    @BeforeEach
    void setup(){
        store = mock();
        methodCache = mock();
        typeCache = mock();
        methodAnalyzer = new MethodAnalyzer(store, methodCache, typeCache);
    }

    @Test
    void createMethodForClass(){
        List<MethodModel> methodModels = createMethodModels(1);
        ClassModel classModel = createClassModel(methodModels);
        FileModel fileModel = createFileModel(classModel, "Relative.Path");

        ClassDescriptorImpl classDescriptor = new ClassDescriptorImpl("Type");
        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.of(classDescriptor));
        when(methodCache.create(any(), eq(MethodDescriptor.class))).thenReturn(new MethodDescriptorImpl());

        methodAnalyzer.createMethods(toList(fileModel));

        verify(methodCache).create(any(), eq(MethodDescriptor.class));
        assertThat(classDescriptor.getDeclaredMembers().size()).isEqualTo(1);
        MemberDescriptor descriptor = classDescriptor.getDeclaredMembers().get(0);
        assertMemberDescriptorHasBeenFilled(descriptor);
    }

    @Test
    void createMethodForClassNonExistentType(){
        List<MethodModel> methodModels = createMethodModels(1);
        ClassModel classModel = createClassModel(methodModels);
        FileModel fileModel = createFileModel(classModel, "Relative.Path");

        ClassDescriptorImpl classDescriptor = new ClassDescriptorImpl("Type");
        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.empty());

        methodAnalyzer.createMethods(toList(fileModel));

        verify(methodCache, never()).create(any(), eq(MethodDescriptor.class));
        assertThat(classDescriptor.getDeclaredMembers().size()).isEqualTo(0);
    }


    @Test
    void createMethodForInterface(){
        List<MethodModel> methodModels = createMethodModels(1);
        InterfaceModel interfaceModel = createInterfaceModel(methodModels);
        FileModel fileModel = createFileModel(interfaceModel, "Relative.Path");

        InterfaceTypeDescriptor interfaceDescriptor = new InterfaceDescriptorImpl("Interface");
        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.of(interfaceDescriptor));
        when(methodCache.create(any(), eq(MethodDescriptor.class))).thenReturn(new MethodDescriptorImpl());

        methodAnalyzer.createMethods(toList(fileModel));

        verify(methodCache).create(any(), eq(MethodDescriptor.class));
        assertThat(interfaceDescriptor.getDeclaredMembers().size()).isEqualTo(1);
        MemberDescriptor descriptor = interfaceDescriptor.getDeclaredMembers().get(0);
        assertMemberDescriptorHasBeenFilled(descriptor);
    }

    @Test
    void createMethodForInterfaceNonexistentType(){
        List<MethodModel> methodModels = createMethodModels(1);
        InterfaceModel interfaceModel = createInterfaceModel(methodModels);
        FileModel fileModel = createFileModel(interfaceModel, "Relative.Path");

        InterfaceTypeDescriptor interfaceDescriptor = new InterfaceDescriptorImpl("Interface");
        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.empty());

        methodAnalyzer.createMethods(toList(fileModel));

        verify(methodCache, never()).create(any(), eq(MethodDescriptor.class));
        assertThat(interfaceDescriptor.getDeclaredMembers().size()).isEqualTo(0);
    }

    @Test
    void createNoMethods(){
        List<MethodModel> methodModels = createMethodModels(0);
        ClassModel classModel = createClassModel(methodModels);
        FileModel fileModel = createFileModel(classModel, "Relative.Path");

        ClassDescriptorImpl classDescriptor = new ClassDescriptorImpl("Class1");
        when(typeCache.findTypeByRelativePath(any(), eq("Relative.Path"))).thenReturn(Optional.of(classDescriptor));

        methodAnalyzer.createMethods(toList(fileModel));

        verify(methodCache, never()).create(any(), eq(ConstructorDescriptor.class));
        assertThat(classDescriptor.getDeclaredMembers().size()).isEqualTo(0);
    }

    @Test
    void createConstructorsOfExistingType(){
        List<MethodModel> methodModels = createMethodModels(0);
        ClassModel classModel = createClassModel(methodModels);
        addConstructor(classModel);
        FileModel fileModel = createFileModel(classModel, "Relative.Path");

        ClassDescriptorImpl classDescriptor = new ClassDescriptorImpl("Class1");
        when(typeCache.findTypeByRelativePath(any(), eq("Relative.Path"))).thenReturn(Optional.of(classDescriptor));
        when(methodCache.create(any(), eq(ConstructorDescriptor.class))).thenReturn(new ConstructorDescriptorImpl());

        methodAnalyzer.createConstructors(toList(fileModel));

        verify(methodCache).create(any(), eq(ConstructorDescriptor.class));
        assertThat(classDescriptor.getDeclaredMembers().size()).isEqualTo(1);
        assertThat(classDescriptor.getDeclaredMembers().get(0)).isInstanceOfAny(ConstructorDescriptor.class);
    }

    @Test
    void createConstructorsOfNonexistentType(){
        List<MethodModel> methodModels = createMethodModels(0);
        ClassModel classModel = createClassModel(methodModels);
        FileModel fileModel = createFileModel(classModel, "Relative.Path");

        when(typeCache.findTypeByRelativePath(any(), any())).thenReturn(Optional.empty());

        methodAnalyzer.createConstructors(toList(fileModel));

        verify(methodCache, never()).create(any(), eq(ConstructorDescriptor.class));
    }

    private static void assertMemberDescriptorHasBeenFilled(MemberDescriptor descriptor) {
        assertThat(descriptor).isNotNull();
        assertThat(descriptor).isInstanceOfAny(MethodDescriptor.class);
        MethodDescriptor methodDescriptor = (MethodDescriptor) descriptor;
        assertThat(methodDescriptor.getEffectiveLineCount()).isNotNull();
        assertThat(methodDescriptor.getLastLineNumber()).isNotNull();
        assertThat(methodDescriptor.getFirstLineNumber()).isNotNull();
        assertThat(methodDescriptor.getName()).isNotNull();
        assertThat(methodDescriptor.getFullQualifiedName()).isNotNull();
        assertThat(methodDescriptor.getAccessibility()).isNotNull();
        assertThat(methodDescriptor.getCyclomaticComplexity()).isNotNull();
    }

    private void addConstructor(ClassModel classModel) {
        ConstructorModel constructorModel = new ConstructorModel();
        fillMethodModel(constructorModel, 2);
        List<ConstructorModel> constructorModels = new ArrayList<>();
        constructorModels.add(constructorModel);
        classModel.setConstructors(constructorModels);
    }

    private List<MethodModel> createMethodModels(int amount) {
        List<MethodModel> methodModels = new LinkedList<>();
        for (int i = 0; i<amount; i++) {
            MethodModel methodModel = new MethodModel();
            fillMethodModel(methodModel, i);
            methodModels.add(methodModel);
        }
        return methodModels;
    }

    private InterfaceModel createInterfaceModel(List<MethodModel> methodModels) {
        InterfaceModel interfaceModel = new InterfaceModel();
        List<MethodModel> methods = new ArrayList<>(methodModels);
        interfaceModel.setMethods(methods);
        interfaceModel.setFqn("Interface");

        return interfaceModel;
    }

    private ClassModel createClassModel(List<MethodModel> methodModels){
        ClassModel classModel = new ClassModel();
        List<MethodModel> methods = new ArrayList<>(methodModels);
        classModel.setMethods(methods);
        classModel.setFqn("Class");

        return classModel;
    }

    private FileModel createFileModel(InterfaceModel interfaceModel, String relativePath){
        FileModel fileModel = new FileModel();
        List<InterfaceModel> interfaceModels = toList(interfaceModel);
        fileModel.setClasses(new ArrayList<>());
        fileModel.setInterfaces(interfaceModels);
        fileModel.setRelativePath(relativePath);

        return fileModel;
    }

    private FileModel createFileModel(ClassModel classModel, String relativePath) {
        FileModel fileModel = new FileModel();
        List<ClassModel> classModels = toList(classModel);
        fileModel.setClasses(classModels);
        fileModel.setInterfaces(new ArrayList<>());
        fileModel.setRelativePath(relativePath);

        return fileModel;
    }

    void fillMethodModel(MethodModel methodModel, int seed){
        methodModel.setEffectiveLineCount(seed);
        methodModel.setLastLineNumber(seed);
        methodModel.setFirstLineNumber(seed);
        methodModel.setName("Method" + seed);
        methodModel.setFqn("FQN.Method." + seed);
        methodModel.setAccessibility(seed % 2 == 0 ? "private" : "public");
        methodModel.setCyclomaticComplexity(seed);
        methodModel.setImplementation(seed % 2 == 1);
        methodModel.setParameters(new ArrayList<>());
    }

    private <T> List<T> toList(T item){
        List<T> list = new ArrayList<>();
        list.add(item);
        return list;
    }
}
