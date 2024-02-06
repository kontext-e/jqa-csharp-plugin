using System;
using Project_1.ExtensionMethods;
using Project_1.Partiality;
using Project1.Types;

namespace Project_1;

public class Invocations
{
    private string _field;
    private int Property { get; set; }
    
    public Invocations()
    {
        if (Method())
        {
            Property = 3;
        }
        else
        {
            Console.WriteLine(_field);
        }

        var staticProperty = Properties.StaticProperty;
        var partialClass = new PartialClass();
        partialClass.PartialMethod();
        var typeClass = new TypeClass(staticProperty);
        typeClass.ExtensionMethodWithArgument(0.5);
        var method = new TypeStruct("String")
        {
            Property = 3
        };
        var property = Property;
    }

    private bool Method() => true;
}