using System;

namespace Project_1.Partiality
{
    public partial class PartialClass
    {
        private partial int PartialMethod() { return 0; }
    }
    
    public partial class PartialClass
    {
        private partial int PartialMethod();
        partial void UnimplementedMethod();
    }
}