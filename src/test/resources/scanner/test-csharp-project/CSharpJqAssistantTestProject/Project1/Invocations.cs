using System;
using System.Collections.Generic;
using Project1.ExtensionMethods;
using Project1.Partiality;
using Project1.Types;

namespace Project1;

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

        var partialClass = new PartialClass();
        partialClass.PartialMethod(); //Tests Call to Partial Method

        var typeClass = new TypeClass(staticProperty);
        typeClass.ExtensionMethodWithArgument(0.5); //Tests Extension Method Call

        var method = new TypeStruct("String") 
        {
            Property = 3 //Tests PropertyAccess in Type Initializer
        };

        var property = Property;
        Method(); //Test Second Call to same Method

        var func = (int number) => { return (number % 2) == 0; };
        bool is2Even = Methods.MethodWithFunctionAsArgument(func); //Tests Function as Argument
        Methods.MethodWithActionAsArgument(Console.WriteLine); //Tests Action As Argument
        
    }

    public static void ArrayCreations()
    {
        int[] array1, array3 = new int[5]; // Declare a single-dimensional array of 5 integers
        int[] array2 = [1, 2, 3, 4, 5, 6]; // Declare and set array element values
        int[,] multiDimensionalArray1 = new int[2, 3]; // Declare a two dimensional array
        int[,] multiDimensionalArray2 = { { 1, 2, 3 }, { 4, 5, 6 } }; // Declare and set array element values
        int[][] jaggedArray = new int[6][]; // Declare a jagged array
        var b = new[] { "hello", null, "world" }; // Implicitly Typed Array Creations | Also Tests for nullable type, as this array is of type string?[] as there is a null in the initializer
        jaggedArray[0] = [1, 2, 3, 4]; // Set the values of the first array in the jagged array structure
    }

    private static void ObjectCreations(Properties prop)
    {
        var partialClass = new PartialClass(); //Tests Object Create Syntax
        var typeClass = new TypeClass(Properties.StaticProperty); //Tests Normal Constructor Call
        var properties = new Properties(); //Tests Default Constructor
        Properties properties2 = new(); //Tests Implicit Object Creation in assignment
        properties2 = new();
        ObjectCreations(new()); //Tests Implicit Object Creation in Expression Statement
        var list = new List<int> { 3, 8 };
        var method = new TypeStruct("String") //Tests Type Initializer
        {
            Property = 3
        };
    }

    private static void CallOverloadedMethod()
    {
        Dependency1.DoSomething(3.5f);
        Dependency1.DoSomething(new Properties());
        // MethodOverloads.OverloadedMethod(123.567f);
        // MethodOverloads.OverloadedMethod( new[] { "Hello" }, 123.567f);
    }

    private void Foo()
    {
        var nonGenericClass = new NonGenericClass();
        var methods = new Methods();
    }

    private void InvocationOfGenericMethods()
    {
        var properties = new Properties();
        var nonGenericClass = new NonGenericClass();
        nonGenericClass.GenericMethodInNonGenericClass(new List<Properties>{properties});
    }

    private static bool Method() => true;
}