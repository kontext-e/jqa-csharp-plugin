using System;

namespace Project1.Types;

public sealed class TypeClass
{
    private string _field;
    private int Property { get; set; }

    public TypeClass(string field)
    {
        _field = field;
    }

    public void Method()
    {
        Console.WriteLine("Hello World");
    }
    
}