using Project1.ExtensionMethods;
using Project1.Partiality;
using Project1.Types;

namespace Project1.DepdenDencies;

public class DependsOnRelation : PartialClass
{
    private readonly TypeClass _class;

    public DependsOnRelation()
    {
        _class = new TypeClass("string");
    }

    private void CallExtensionMethod()
    {
        _class.ExtensionMethodWithArgument(0.9);
    }
}