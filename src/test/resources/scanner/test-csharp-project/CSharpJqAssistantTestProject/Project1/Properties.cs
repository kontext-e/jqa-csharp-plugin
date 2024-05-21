using System.Collections.Generic;

namespace Project1
{
    public class Properties
    {
        private string _backingField;

        int ImplicitlyPrivateProperty { get; init; }
        public string ExplicitlyPublicProperty { get; }
        private protected int PrivateProtectedProperty { get; }
        public string PropertyWithDifferingAccessorAccessibility { get; private set; }

        public static string StaticProperty { get; }
    
        public string ExpressionBodiedProperty => "Empty String";
        public string PropertyWithExplicitAccessors
        {
            get { return "Another String"; }
            set => _backingField = value;
        }
        
        public List<List<string>> NestedGenericProperty { get; set; } 
        public List<string?>? GenericNullableProperty { get; set; }
        
    }

    public record ReadOnlyRecord(int A, int B);

    public record struct WriteableRecordStruct(float F);

    public readonly record struct ReadOnlyRecordStruct(string A, string B);
}