using System;

namespace Project_1;

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

}

public static class MethodExtensions
{
    public static void ExtensionMethod(this Methods methods, int number) {}
}