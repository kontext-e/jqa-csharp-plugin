package org.jqassistant.contrib.plugin.csharp.json_to_neo4j;

import com.buschmais.jqassistant.plugin.common.api.model.FileDescriptor;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.CSharpFileCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.EnumValueCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.NamespaceCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.caches.TypeCache;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.ClassModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.EnumModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.FileModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.InterfaceModel;
import org.jqassistant.contrib.plugin.csharp.json_to_neo4j.json_model.TypeModel;
import org.jqassistant.contrib.plugin.csharp.model.CSharpFileDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.ClassDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.EnumTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.InterfaceTypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.MemberDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.NamespaceDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.TypeDescriptor;
import org.jqassistant.contrib.plugin.csharp.model.UsesNamespaceDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
        when(mockTypeCache.findAllPartialClasses()).thenReturn(partialityList);

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
        FileModel fileModel = mock();

        List<ClassModel> classModels = createTypeModelList(amountOfClasses, ClassModel.class);
        List<InterfaceModel> interfaceModels = createTypeModelList(amountOfInterfaces, InterfaceModel.class);
        List<EnumModel> enumModels = createTypeModelList(amountOfEnums, EnumModel.class);

        when(fileModel.getClasses()).thenReturn(classModels);
        when(fileModel.getInterfaces()).thenReturn(interfaceModels);
        when(fileModel.getEnums()).thenReturn(enumModels);
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
        assertThat(interfaceDescriptor.getVisibility()).isNotNull();
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

    private static class TypeDescriptorImpl implements TypeDescriptor {

        private TypeDescriptorImpl(String name){
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

    private static class ClassDescriptorImpl extends TypeDescriptorImpl implements ClassDescriptor {

        private ClassDescriptorImpl(String name) {
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

    private static class InterfaceDescriptorImpl extends TypeDescriptorImpl implements InterfaceTypeDescriptor {
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

        private InterfaceDescriptorImpl(String name) {
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

    private static class EnumDescriptorImpl extends ClassDescriptorImpl implements EnumTypeDescriptor{
        private EnumDescriptorImpl(String name) {
            super(name);
        }
    }

    private static class NamespaceDescriptorImpl implements NamespaceDescriptor{

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

    private static class CSharpFileDescriptorImpl implements CSharpFileDescriptor{

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
}