using System;

namespace Project1.Types;

public readonly struct TypeStruct
{
    private readonly string _field;
    public int Property { get; init; }

    public TypeStruct(string field)
    {
        _field = field;
    }

    public void Method()
    {
        Console.WriteLine("Hello World");
    }
}