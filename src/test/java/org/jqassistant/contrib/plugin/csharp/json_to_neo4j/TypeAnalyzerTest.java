package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.EnumValueCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.EnumMemberModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.EnumModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.TypeModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.CSharpFileDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.ClassDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.EnumDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.EnumValueDescriptorIml;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.InterfaceDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.NamespaceDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.testImplementations.TypeDescriptorImpl;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TypeAnalyzerTest {

    private TypeAnalyzer typeAnalyzer;

    private TypeCache typeCacheMock;
    private NamespaceCache namespaceCacheMock;
    private final CSharpFileCache cSharpFileCacheMock = mock();
    private final EnumValueCache enumValueCacheMock = mock(EnumValueCache.class);

    private List<TypeDescriptor> typeDescriptors;


    @BeforeEach
    void setUp() {
        typeDescriptors = createTypeDescriptor();
        typeCacheMock = createTypeCacheMock();
        List<NamespaceDescriptor> namespaceDescriptors = createNamespaces(typeDescriptors);
        namespaceCacheMock = createNameSpaceCacheMock(namespaceDescriptors);

        typeAnalyzer = new TypeAnalyzer(
                namespaceCacheMock,
                cSharpFileCacheMock,
                enumValueCacheMock,
                typeCacheMock
        );
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

    private TypeCache createTypeCacheMock() {
        TypeCache mockTypeCache = mock();

        when(mockTypeCache.create(any(ClassModel.class))).thenReturn(new ClassDescriptorImpl("newClass"));
        when(mockTypeCache.create(any(InterfaceModel.class))).thenReturn(new InterfaceDescriptorImpl("newInterface"));
        when(mockTypeCache.create(any(EnumModel.class))).thenReturn(new EnumDescriptorImpl("newEnum"));

        List<TypeDescriptor> descriptorList = new ArrayList<>();
        descriptorList.add(typeDescriptors.get(0));
        descriptorList.add(typeDescriptors.get(1));
        List<List<TypeDescriptor>> partialityList = new ArrayList<>();
        partialityList.add(descriptorList);
        when(mockTypeCache.findAllPartialTypes()).thenReturn(partialityList);

        return mockTypeCache;
    }


    private static List<NamespaceDescriptor> createNamespaces(List<TypeDescriptor> typeDescriptors) {
        NamespaceDescriptor namespaceDescriptor = new NamespaceDescriptorImpl();
        namespaceDescriptor.getContains().addAll(typeDescriptors);
        List<NamespaceDescriptor> namespaces = new LinkedList<>();
        namespaces.add(namespaceDescriptor);

        return namespaces;
    }

    private static NamespaceCache createNameSpaceCacheMock(List<NamespaceDescriptor> namespaceDescriptorMocks) {
        NamespaceCache mockNamespaceCache = mock(NamespaceCache.class);
        when(mockNamespaceCache.getAllNamespaces()).thenReturn(namespaceDescriptorMocks);
        when(mockNamespaceCache.findOrCreate(any())).thenReturn(namespaceDescriptorMocks.get(0));

        return mockNamespaceCache;
    }

    private static FileModel createFileModel(int amountOfClasses, int amountOfInterfaces, int amountOfEnums) {
        FileModel fileModel = new FileModel();

        List<ClassModel> classModels = createTypeModelList(amountOfClasses, ClassModel.class);
        List<InterfaceModel> interfaceModels = createTypeModelList(amountOfInterfaces, InterfaceModel.class);
        List<EnumModel> enumModels = createTypeModelList(amountOfEnums, EnumModel.class);

        fileModel.setClasses(classModels);
        fileModel.setInterfaces(interfaceModels);
        fileModel.setEnums(enumModels);
        return fileModel;
    }

    private static <T extends TypeModel> List<T> createTypeModelList(int amount, Class<T> tClass){
        List<T> list = new ArrayList<>();
        for (int i = 0;  i < amount; i++){
            T type = mock(tClass);
            createTypeModel(tClass, i, type);
            list.add(type);
        }
        return list;
    }

    private static <T extends TypeModel> void createTypeModel(Class<T> tClass, int i, T type) {
        when(type.getName()).thenReturn(tClass.toString() + i);
        when(type.getFqn()).thenReturn(tClass.toString() + i);
        when(type.getMd5()).thenReturn(tClass.toString() + i);
        when(type.getFirstLineNumber()).thenReturn(5 + i);
        when(type.getLastLineNumber()).thenReturn(10 + i);
        when(type.getEffectiveLineCount()).thenReturn(5*i);
        when(type.getRelativePath()).thenReturn(tClass.toString() + i);
        when(type.getName()).thenReturn(tClass.toString() + i);

        if (tClass == InterfaceModel.class){
            InterfaceModel interfaceModel = (InterfaceModel) type;
            when(interfaceModel.isPartial()).thenReturn(false);
            when(interfaceModel.getAccessibility()).thenReturn("public");
        }

        if (tClass == ClassModel.class){
            ClassModel classModel = (ClassModel) type;
            when(classModel.isPartial()).thenReturn(false);
            when(classModel.isAbstractKeyword()).thenReturn(false);
            when(classModel.isSealed()).thenReturn(false);
            when(classModel.isStaticKeyword()).thenReturn(false);
        }

        if (tClass == EnumModel.class){
            EnumModel enumModel = (EnumModel) type;
            when(enumModel.getKey()).thenReturn(tClass.toString() + i);

            EnumMemberModel enumMemberModel = mock();
            when(enumMemberModel.getKey()).thenReturn(Integer.toString(i));
            List<EnumMemberModel> memberModels = new LinkedList<>();
            memberModels.add(enumMemberModel);

            when(enumModel.getMembers()).thenReturn(memberModels);
        }
    }

    @Test
    void testCreateEnums(){
        CSharpFileDescriptor cSharpFileDescriptor = new CSharpFileDescriptorImpl("file", "fileName");
        doReturn(cSharpFileDescriptor).when(cSharpFileCacheMock).get(any());
        int numberOfEnumModels = 1;

        FileModel fileModel = createFileModel(0, 0, numberOfEnumModels);
        LinkedList<FileModel> fileModels = new LinkedList<>();
        fileModels.add(fileModel);

        typeAnalyzer.createTypes(fileModels);

        verify(typeCacheMock).create(any(EnumModel.class));
        verify(namespaceCacheMock).findOrCreate(any());
        assertThat(cSharpFileDescriptor.getTypes().size()).isEqualTo(numberOfEnumModels);

        TypeDescriptor typeDescriptor = cSharpFileDescriptor.getTypes().get(0);
        assertThat(typeDescriptor.getName()).isNotNull();
        assertThat(typeDescriptor.getFullQualifiedName()).isNotNull();
        assertThat(typeDescriptor.getMd5()).isNotNull();
        assertThat(typeDescriptor.getRelativePath()).isNotNull();
        assertThat(typeDescriptor.getFirstLineNumber()).isNotNull();
        assertThat(typeDescriptor.getLastLineNumber()).isNotNull();
        assertThat(typeDescriptor.getEffectiveLineCount()).isNotNull();
    }

    @Test
    void testCreateClasses(){
        CSharpFileDescriptor cSharpFileDescriptor = new CSharpFileDescriptorImpl("file", "fileName");
        doReturn(cSharpFileDescriptor).when(cSharpFileCacheMock).get(any());
        int numberOfClasses = 1;

        FileModel fileModel = createFileModel(numberOfClasses, 0, 0);
        LinkedList<FileModel> fileModels = new LinkedList<>();
        fileModels.add(fileModel);

        typeAnalyzer.createTypes(fileModels);

        verify(typeCacheMock).create(any(ClassModel.class));
        verify(namespaceCacheMock).findOrCreate(any());
        assertThat(cSharpFileDescriptor.getTypes().size()).isEqualTo(numberOfClasses);

        TypeDescriptor typeDescriptor = cSharpFileDescriptor.getTypes().get(0);
        ClassDescriptor classDescriptor = (ClassDescriptor) typeDescriptor;
        assertThat(classDescriptor.getPartial()).isNotNull();
        assertThat(classDescriptor.isAbstract()).isNotNull();
        assertThat(classDescriptor.isSealed()).isNotNull();
        assertThat(classDescriptor.isStatic()).isNotNull();
    }


    @Test
    void testCreateInterfaces() {
        CSharpFileDescriptor cSharpFileDescriptor = new CSharpFileDescriptorImpl("file", "fileName");
        doReturn(cSharpFileDescriptor).when(cSharpFileCacheMock).get(any());
        int numberOfInterfaces = 1;

        FileModel fileModel = createFileModel(0, numberOfInterfaces, 0);
        LinkedList<FileModel> fileModels = new LinkedList<>();
        fileModels.add(fileModel);

        typeAnalyzer.createTypes(fileModels);

        verify(typeCacheMock).create(any(InterfaceModel.class));
        verify(namespaceCacheMock).findOrCreate(any());
        assertThat(cSharpFileDescriptor.getTypes().size()).isEqualTo(1);

        TypeDescriptor typeDescriptor = cSharpFileDescriptor.getTypes().get(0);
        InterfaceTypeDescriptor interfaceDescriptor = (InterfaceTypeDescriptor) typeDescriptor;
        assertThat(interfaceDescriptor.getAccessibility()).isNotNull();
        assertThat(interfaceDescriptor.getPartial()).isNotNull();
    }

    @Test
    void testCreateTypeWithDefaultNamespace(){
        CSharpFileDescriptor cSharpFileDescriptor = new CSharpFileDescriptorImpl("file", "fileName");
        doReturn(cSharpFileDescriptor).when(cSharpFileCacheMock).get(any());

        FileModel fileModel = createFileModel(1, 0, 0);
        ClassModel classes = fileModel.getClasses().get(0);
        when(classes.getFqn()).thenReturn("withoutDot");
        LinkedList<FileModel> fileModels = new LinkedList<>();
        fileModels.add(fileModel);

        typeAnalyzer.createTypes(fileModels);

        verify(typeCacheMock).create(any(ClassModel.class));
        verify(namespaceCacheMock, never()).findOrCreate(any());
        assertThat(cSharpFileDescriptor.getTypes().size()).isEqualTo(1);
    }

    @Test
    void testCreateEnumMembers(){
        List<EnumModel> enums = createTypeModelList(2, EnumModel.class);
        when(typeCacheMock.findAny(any())).thenReturn(new EnumDescriptorImpl("EnumName"));
        when(enumValueCacheMock.create(any())).thenReturn(new EnumValueDescriptorIml());

        enums.forEach(typeAnalyzer::createEnumMembers);

        verify(typeCacheMock, times(2)).findAny(any());
        verify(enumValueCacheMock, times(2)).create(any());
    }


}