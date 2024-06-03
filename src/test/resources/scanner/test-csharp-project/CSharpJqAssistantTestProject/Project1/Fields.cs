using System.Collections.Generic;

namespace Project1;

public class Fields
{
    private string privateField;
    public int PublicField;
    internal static Fields StaticField;
    public required bool RequiredField;
    protected volatile float VolatileField;
    protected internal const string DefaultField = "Initial String";
    protected internal const int DefaultInt = 0, DefaultSomething = 3;
}

public class GenericFields<T, TS> where T : Properties where TS : List<T>
{
    private List<T> GenericList { get; set; }

    private TS GenericField;
    
    protected Dictionary<string[], TS[]> NestedGenericField;

    private TS[] GenericArrayField;

    private string[] nonGenericArrayField;
}

public class EvenMoreGeneric<T>
{
    private T evenMoreGenericField;
}

public class NonGenericClass
{
    protected T GenericMethodInNonGenericClass<T>(T input)
    {
        return input;
    }
    
    protected T GenericMethodInNonGenericClass2<T>(T input) where T : Properties
    {
        return input;
    }
    
}