using System;

namespace Project1.Types;

public interface ITypeInterface
{
    public int Property { get; set; }

    public void Method()
    {
        Console.WriteLine("Hello World");
    }
}