package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.apache.commons.lang.NotImplementedException;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.EnumValueCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.EnumModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.TypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.UsingModel;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TypeAnalyzerTest {

    private TypeAnalyzer typeAnalyzer;
    private Store mockStore;
    private TypeCache typeCacheMock;
    private CSharpFileCache cSharpFileCacheMock;
    private final EnumValueCache enumValueCacheMock = mock(EnumValueCache.class);
    private List<TypeDescriptor> typeDescriptors;
    private List<NamespaceDescriptor> namespaceDescriptorMocks;
    private NamespaceCache namespaceCacheMock;

    private List<FileModel> fileModelList;

    @BeforeEach
    void setUp() {
        fileModelList = createFileModelList();
        mockStore = createMockStore();
        typeDescriptors = createTypeDescriptor();
        typeCacheMock = createTypeCacheMock();
        cSharpFileCacheMock = createCSharpFileCacheMock();
        namespaceDescriptorMocks = createNamespaceMocks(typeDescriptors);
        namespaceCacheMock = createNameSpaceCacheMock(namespaceDescriptorMocks);

        typeAnalyzer = new TypeAnalyzer(
                mockStore,
                namespaceCacheMock,
                cSharpFileCacheMock,
                enumValueCacheMock,
                typeCacheMock
        );
    }

    private TypeCache createTypeCacheMock() {
        TypeCache mockTypeCache = mock();
        TypeDescriptor mockTypeDescriptor = mock();
        when(mockTypeCache.create(any())).thenReturn(mockTypeDescriptor);
        List<TypeDescriptor> descriptorList = new ArrayList<>();
        descriptorList.add(typeDescriptors.get(0));
        descriptorList.add(typeDescriptors.get(1));
        List<List<TypeDescriptor>> partialityList = new ArrayList<>();
        partialityList.add(descriptorList);
        when(mockTypeCache.findAllPartialClasses()).thenReturn(partialityList);
        return mockTypeCache;
    }

    private Store createMockStore() {
        Store mockStore = mock();
        UsesNamespaceDescriptor mockUsesNamespaceDescriptor = mock();
        when(mockStore.create(any(), eq(UsesNamespaceDescriptor.class), any())).thenReturn(mockUsesNamespaceDescriptor);
        return mockStore;
    }

    private CSharpFileCache createCSharpFileCacheMock() {
        CSharpFileCache mockCSharpFileCache = mock();
        CSharpFileDescriptor cSharpFileDescriptor = mock(CSharpFileDescriptor.class);
        doReturn(cSharpFileDescriptor).when(mockCSharpFileCache).get(any());
        return mockCSharpFileCache;
    }


    private List<FileModel> createFileModelList() {

        FileModel fileModel = mock();

        List<ClassModel> classModels = createTypeModelList(3, ClassModel.class);
        List<InterfaceModel> interfaceModels = createTypeModelList(2, InterfaceModel.class);
        List<EnumModel> enumModels = createTypeModelList(1, EnumModel.class);

        when(fileModel.getClasses()).thenReturn(classModels);
        when(fileModel.getInterfaces()).thenReturn(interfaceModels);
        when(fileModel.getEnums()).thenReturn(enumModels);

        addUsingsToFileModel(fileModel);

        List<FileModel> fileModelList = new ArrayList<>();
        fileModelList.add(fileModel);

        return fileModelList;
    }

    private void addUsingsToFileModel(FileModel fileModel) {
        UsingModel usingModel1 = mock();
        UsingModel usingModel2 = mock();

        when(usingModel1.getKey()).thenReturn("Class1");
        when(usingModel1.getAlias()).thenReturn("Alias1");
        when(usingModel2.getKey()).thenReturn("Class2");
        when(usingModel2.getAlias()).thenReturn("Alias2");

        LinkedList<UsingModel> usingModels = new LinkedList<>();
        usingModels.add(usingModel1);
        usingModels.add(usingModel2);

        when(fileModel.getUsings()).thenReturn(usingModels);
    }

    private static <T extends TypeModel> List<T> createTypeModelList(int amount, Class<T> tClass){
        List<T> list = new ArrayList<>();
        for (int i = 0;  i < amount; i++){
            T type = mock(tClass);
            when(type.getName()).thenReturn(tClass.toString() + i);
            list.add(type);
        }
        return list;
    }

    private static NamespaceCache createNameSpaceCacheMock(List<NamespaceDescriptor> namespaceDescriptorMocks) {
        NamespaceCache mockNamespaceCache = mock(NamespaceCache.class);
        when(mockNamespaceCache.getAllNamespaces()).thenReturn(namespaceDescriptorMocks);
        when(mockNamespaceCache.findOrCreate(any())).thenReturn(namespaceDescriptorMocks.get(0));

        return mockNamespaceCache;
    }

    private static List<NamespaceDescriptor> createNamespaceMocks(List<TypeDescriptor> typeDescriptors) {
        NamespaceDescriptor namespaceDescriptor = mock(NamespaceDescriptor.class);
        when(namespaceDescriptor.getContains()).thenReturn(typeDescriptors);
        List<NamespaceDescriptor> namespaces = new LinkedList<>();
        namespaces.add(namespaceDescriptor);

        return namespaces;
    }

    private static List<TypeDescriptor> createTypeDescriptor() {

        TypeDescriptor partialClass1 = new TypeDescriptorImpl("Class1");
        TypeDescriptor partialClass2 = new TypeDescriptorImpl("Class1");
        TypeDescriptor nonPartialClass = new TypeDescriptorImpl("Class2");

        List<TypeDescriptor> typeDescriptors = new LinkedList<>();
        typeDescriptors.add(partialClass1);
        typeDescriptors.add(partialClass2);
        typeDescriptors.add(nonPartialClass);

        return typeDescriptors;
    }

    @Test
    void testCreateTypes(){
        TypeAnalyzer spyTypeAnalyzer = spy(typeAnalyzer);
        doNothing().when(spyTypeAnalyzer).createType(any(CSharpFileDescriptor.class), any(TypeModel.class));

        spyTypeAnalyzer.createTypes(fileModelList);

        verify(spyTypeAnalyzer, times(3)).createType(any(CSharpFileDescriptor.class), any(ClassModel.class));
        verify(spyTypeAnalyzer, times(2)).createType(any(CSharpFileDescriptor.class), any(InterfaceModel.class));
        verify(spyTypeAnalyzer, times(1)).createType(any(CSharpFileDescriptor.class), any(EnumModel.class));
    }


//    @Test
//    void testLinkPartialClassesTest() {
//
//        typeAnalyzer.linkPartialClasses();
//
//        assertThat(typeDescriptors.get(0).getClassFragments().size()).isEqualTo(1);
//        assertThat(typeDescriptors.get(1).getClassFragments().size()).isEqualTo(1);
//        assertThat(typeDescriptors.get(2).getClassFragments().size()).isEqualTo(0);
//        assertThat(typeDescriptors.get(0).getClassFragments().get(0)).isEqualTo(typeDescriptors.get(1));
//        assertThat(typeDescriptors.get(1).getClassFragments().get(0)).isEqualTo(typeDescriptors.get(0));
//    }

    @Test
    void testCreateUsings() {
        TypeAnalyzer spyTypeAnalyzer = spy(typeAnalyzer);

        spyTypeAnalyzer.createUsings(fileModelList);

        verify(mockStore, times(2)).create(any(), eq(UsesNamespaceDescriptor.class), any());
    }

    @Test
    void testCreateType(){
        CSharpFileDescriptor descriptor = mock();
        TypeModel typeModel = mock();
        List<TypeDescriptor> descriptors = mock();
        when(typeModel.getFqn()).thenReturn("test.type");
        when(descriptor.getTypes()).thenReturn(descriptors);

        TypeAnalyzer spyTypeAnalyzer = spy(typeAnalyzer);
        spyTypeAnalyzer.createType(descriptor, typeModel);

        verify(typeCacheMock).create(typeModel);
        verify(spyTypeAnalyzer).fillDescriptor(any(TypeDescriptor.class), eq(typeModel));
        verify(descriptor.getTypes()).add(any(TypeDescriptor.class));
        verify(spyTypeAnalyzer).findOrCreateNamespace(any());
    }

    @Test
    void testFindOrCreateNamespace(){
        NamespaceDescriptor namespaceDescriptorMock = mock();
        when(namespaceCacheMock.findOrCreate(any())).thenReturn(namespaceDescriptorMock);

        Optional<NamespaceDescriptor> emptyOptional = typeAnalyzer.findOrCreateNamespace("test");
        assertThat(emptyOptional.isPresent()).isFalse();

        Optional<NamespaceDescriptor> filledOptional = typeAnalyzer.findOrCreateNamespace("test.type");
        verify(namespaceCacheMock, atMostOnce()).findOrCreate("test");
        assertThat(filledOptional.isPresent()).isTrue();

    }

    private static class TypeDescriptorImpl implements TypeDescriptor {

        private final List<TypeDescriptor> classFragments = new LinkedList<>();
        private final String name;

        private TypeDescriptorImpl(String name){
            this.name = name;
        }

        @Override
        public List<MemberDescriptor> getDeclaredMembers() {
            throw new NotImplementedException();
        }

        @Override
        public Integer getFirstLineNumber() {
            throw new NotImplementedException();
        }

        @Override
        public void setFirstLineNumber(Integer firstLineNumber) {
            throw new NotImplementedException();
        }

        @Override
        public Integer getLastLineNumber() {
            throw new NotImplementedException();
        }

        @Override
        public void setLastLineNumber(Integer lastLineNumber) {
            throw new NotImplementedException();
        }

        @Override
        public Integer getEffectiveLineCount() {
            throw new NotImplementedException();
        }

        @Override
        public void setEffectiveLineCount(Integer effectiveLineCount) {
            throw new NotImplementedException();
        }

        @Override
        public List<TypeDescriptor> getClassFragments() {
            return classFragments;
        }

        @Override
        public String getFullQualifiedName() {
            throw new NotImplementedException();
        }

        @Override
        public void setFullQualifiedName(String s) {
            throw new NotImplementedException();
        }

        @Override
        public String getMd5() {
            throw new NotImplementedException();
        }

        @Override
        public void setMd5(String s) {
            throw new NotImplementedException();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String s) {
            throw new NotImplementedException();
        }

        @Override
        public <I> I getId() {
            throw new NotImplementedException();
        }

        @Override
        public <T> T as(Class<T> aClass) {
            throw new NotImplementedException();
        }

        @Override
        public <D> D getDelegate() {
            throw new NotImplementedException();
        }

        @Override
        public void setRelativePath(String path) {
            throw new NotImplementedException();
        }

        @Override
        public String getRelativePath() {
            throw new NotImplementedException();
        }

        @Override
        public void setPartial(boolean partial) {
            throw new NotImplementedException();
        }

        @Override
        public boolean getPartial() {
            throw new NotImplementedException();
        }
    }

}