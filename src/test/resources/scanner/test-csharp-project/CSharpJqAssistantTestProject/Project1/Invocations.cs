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
        if (Method()) //Tests Call to own Method
        {
            Property = 3; //Tests Call to own Property
        }
        else
        {
            Console.WriteLine(_field); //Tests Call to static Method
        }

        var staticProperty = Properties.StaticProperty; //Tests Call to others' Type Property
        var partialClass = new PartialClass(); //Tests Object Createn Syntax
        partialClass.PartialMethod(); //Tests Call to Partial Method
        var typeClass = new TypeClass(staticProperty); //Tests Normal Constructor Call
        typeClass.ExtensionMethodWithArgument(0.5); //Tests Extension Method Call
        var method = new TypeStruct("String") //Tests Type Initializer
        {
            Property = 3
        };
        var property = Property;
        var func = (int number) => { return (number % 2) == 0; };
        bool is2Even = Methods.MethodWithFunctionAsArgument(func); //Tests Function as Argument
        Methods.MethodWithActionAsArgument(Console.WriteLine); //Tests Action As Argument
        var properties = new Properties(); //Tests Default Constructor
        Properties properties2 = new(); //Tests Implicit Object Creation in assignment
        properties2 = new();
        ImplicitObjectCreation(new()); //Tests Implicit Object Creation in Exoression Statment
    }

    private static void ImplicitObjectCreation(Properties properties)
    {

    }

    private bool Method() => true;
}