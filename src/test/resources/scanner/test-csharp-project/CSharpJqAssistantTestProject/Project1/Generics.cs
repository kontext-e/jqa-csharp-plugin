using System.Collections.Generic;

namespace Project1;

public class GenericFields<T, TS> where T : Properties where TS : List<T>
{
    private T GenericProperty { get; set; }
    private TS GenericField;

    protected List<T> GenericNestedProperty { get; set; }
    protected Dictionary<string[], TS[]>? NestedGenericField;

    private TS[]? GenericArrayField;
    private string[] nonGenericArrayField;

    public T MethodWithConstrainedReturnType(T parameter) => parameter;
    public List<TS>? MethodWithNestedGenericReturnType(TS parameter) => [parameter];
}

public class EvenMoreGeneric<T>
{
    private T GenericFieldWithoutConstraint;
    private T MethodWithUnconstrainedReturnType(T parameter) => parameter;
}

public class NonGenericClass
{
    protected T GenericMethodInNonGenericClass<T>(T input) => input;
    protected T GenericMethodInNonGenericClassWithConstraints<T>(T input) where T : Properties => input;
    private TRec RecursiveGenericMethod<TRec>(TRec recursiveArgument) where TRec : EvenMoreGeneric<TRec> => recursiveArgument;
}