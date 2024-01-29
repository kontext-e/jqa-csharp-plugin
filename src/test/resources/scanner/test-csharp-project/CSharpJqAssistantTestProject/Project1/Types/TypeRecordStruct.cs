using System;

namespace Project1.Types;

public record struct TypeRecordStruct
{
    private string _field;
    private int Property { get; set; }

    public TypeRecordStruct(string field)
    {
        _field = field;
    }

    public void Method()
    {
        Console.WriteLine("Hello World");
    }
}