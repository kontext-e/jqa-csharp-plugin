﻿using System;

namespace Project1.Partiality
{
    public partial class PartialClass
    {
        public partial int PartialMethod()
        {
            Invocations.ArrayCreations();
            return 0;
        }
    }
    
    public partial class PartialClass
    {
        public partial int PartialMethod();
        partial void UnimplementedMethod();
    }
}