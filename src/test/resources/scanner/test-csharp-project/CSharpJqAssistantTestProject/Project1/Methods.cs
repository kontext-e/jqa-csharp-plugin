using System;

namespace Project1;

public class Methods
{
    public Methods(){}

    void ImplicitlyPrivateMethod(){}
    public void ExplicitlyPublicMethod(){}
    protected internal void ProtectedInternalMethod(){}

    public void MethodWithMultipleArguments(Methods methods, int number){}
    public void MethodWithDefaultArguments(Methods methods, int number = 2){}

    public int MethodWithReturnType() { return 3; }

    public void MethodWithLocalMethod() { void LocalMethod(){} }
    public void ExpressionMethod(string text) => Console.WriteLine(text);

    public static void MethodWithActionAsArgument(Action<string> action) => action("Hello World");

    public static bool MethodWithFunctionAsArgument(Func<int, bool> func) => func(2); 

}

public static class MethodExtensions
{
    public static void ExtensionMethod(this Methods methods, int number) {}
}