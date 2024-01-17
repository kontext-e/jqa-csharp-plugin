using System;

namespace Project_1;

public class Methods
{
    public Methods(){}
    
    void ImplicitlyPrivateMethod(){}
    public void PublicMethod(){}
    private void PrivateMethod(){}
    protected void ProtectedMethod(){}
    private protected void PrivateProtectedMethod(){}
    internal void InternalMethod(){}
    protected internal void ProtectedInternalMethod(){}
    
    public void MethodWithNoArguments(){}
    public void MethodWithDefaultArguments(Methods methods, int number = 2){}

    public void MethodWithNoReturnType(){}
    public int MethodWithSimpleReturnType() { return 3; }
    public Methods MethodWithTypeReturnType() { return new Methods(); }
    
    public void MethodWithLocalMethod() { void LocalMethod(){} }

    public void ExpressionMethod(string text) => Console.WriteLine(text);
}