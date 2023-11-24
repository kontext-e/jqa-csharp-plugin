using System;

namespace Project_1.Partiality
{
    public partial class Class1 : Class2
    {
        private readonly int _number;
        
        public Class1(string name, int number) : base(name)
        {
            _number = number;
            var returnValue = PartialMethod();
            Console.WriteLine(returnValue);
        }

        private partial int PartialMethod()
        {
            Console.WriteLine(_number);
            return 0;
        }
    }
}