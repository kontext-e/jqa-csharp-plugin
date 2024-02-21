using System;

namespace Project1.Types;

public record TypeRecord
{
    private string _field;
    private int Property { get; set; }

    public TypeRecord(string field)
    {
        _field = field;
    }

    public void Method()
    {
        Console.WriteLine("Hello World");
    }
}