using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Project_1
{
    class Properties
    {
        private string _backingField;

        private int ImplicitlyPrivateProperty { get; init; }
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

    }
}