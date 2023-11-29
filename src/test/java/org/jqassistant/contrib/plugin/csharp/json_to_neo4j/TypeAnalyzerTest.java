package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.core.store.api.Store;
import org.apache.commons.lang.NotImplementedException;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.EnumValueCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.*;
import org.jqassistant.contrib.plugin.csharp.model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TypeAnalyzerTest {

    private TypeAnalyzer typeAnalyzer;
    private JsonToNeo4JConverter jsonToNeo4JConverter;
    private final Store storeMock = mock(Store.class);
    private final TypeCache typeCacheMock = mock(TypeCache.class);
    private CSharpFileCache cSharpFileCacheMock;
    private final EnumValueCache enumValueCacheMock = mock(EnumValueCache.class);
    private List<TypeDescriptor> typeDescriptors;
    private List<NamespaceDescriptor> namespaceDescriptorMocks;
    private NamespaceCache namespaceCacheMock;


    @BeforeAll
    void setUp() {
        jsonToNeo4JConverter = createJsonToNeo4JConverter();
        typeDescriptors = createTypeDescriptor();
        cSharpFileCacheMock = createCSharpFileCacheMock();
        namespaceDescriptorMocks = createNamespaceMocks(typeDescriptors);
        namespaceCacheMock = createNameSpaceCacheMock(namespaceDescriptorMocks);

        typeAnalyzer = new TypeAnalyzer(
                jsonToNeo4JConverter,
                storeMock,
                namespaceCacheMock,
                cSharpFileCacheMock,
                enumValueCacheMock,
                typeCacheMock
        );
    }

    private CSharpFileCache createCSharpFileCacheMock() {
        CSharpFileCache mockCSharpFileCache = mock();
        CSharpFileDescriptor cSharpFileDescriptor = mock(CSharpFileDescriptor.class);
        doReturn(cSharpFileDescriptor).when(mockCSharpFileCache).get(any());
        return mockCSharpFileCache;
    }

    private JsonToNeo4JConverter createJsonToNeo4JConverter() {
        JsonToNeo4JConverter mockJsonToNeo4JConverter = mock();
        List<FileModel> fileModelList = createFileModelList();
        when(mockJsonToNeo4JConverter.getFileModelList()).thenReturn(fileModelList);
        return mockJsonToNeo4JConverter;
    }

    private List<FileModel> createFileModelList() {

        FileModel fileModel1 = mock();

        List<ClassModel> classModels = createTypeModelList(3, ClassModel.class);
        List<InterfaceModel> interfaceModels = createTypeModelList(2, InterfaceModel.class);
        List<EnumModel> enumModels = createTypeModelList(1, EnumModel.class);

        when(fileModel1.getClasses()).thenReturn(classModels);
        when(fileModel1.getInterfaces()).thenReturn(interfaceModels);
        when(fileModel1.getEnums()).thenReturn(enumModels);

        List<FileModel> fileModelList = new ArrayList<>();
        fileModelList.add(fileModel1);

        return fileModelList;
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

        spyTypeAnalyzer.createTypes();

        verify(spyTypeAnalyzer, times(3)).createType(any(CSharpFileDescriptor.class), any(ClassModel.class));
        verify(spyTypeAnalyzer, times(2)).createType(any(CSharpFileDescriptor.class), any(InterfaceModel.class));
        verify(spyTypeAnalyzer, times(1)).createType(any(CSharpFileDescriptor.class), any(EnumModel.class));
    }

    @Test
    void testSortTypesByPartiality(){

        typeAnalyzer.sortTypesByPartiality(namespaceDescriptorMocks.get(0));

        Map<String, List<TypeDescriptor>> partialityMap = typeAnalyzer.sortTypesByPartiality(namespaceDescriptorMocks.get(0));
        assertThat(partialityMap.keySet().size()).isEqualTo(2);
        assertThat(partialityMap.containsKey("Class1")).isTrue();
        assertThat(partialityMap.containsKey("Class2")).isTrue();
        assertThat(partialityMap.get("Class1").size()).isEqualTo(2);
        assertThat(partialityMap.get("Class2").size()).isEqualTo(1);
        assertThat(partialityMap.get("Class1").contains(typeDescriptors.get(0))).isTrue();
        assertThat(partialityMap.get("Class1").contains(typeDescriptors.get(1))).isTrue();
        assertThat(partialityMap.get("Class2").contains(typeDescriptors.get(2))).isTrue();

    }

    @Test
    void testLinkPartialClassesTest() {

        typeAnalyzer.linkPartialClasses();

        verify(namespaceCacheMock).getAllNamespaces();
        assertThat(typeDescriptors.get(0).getClassFragments().size()).isEqualTo(1);
        assertThat(typeDescriptors.get(1).getClassFragments().size()).isEqualTo(1);
        assertThat(typeDescriptors.get(2).getClassFragments().size()).isEqualTo(0);
        assertThat(typeDescriptors.get(0).getClassFragments().get(0)).isEqualTo(typeDescriptors.get(1));
        assertThat(typeDescriptors.get(1).getClassFragments().get(0)).isEqualTo(typeDescriptors.get(0));
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