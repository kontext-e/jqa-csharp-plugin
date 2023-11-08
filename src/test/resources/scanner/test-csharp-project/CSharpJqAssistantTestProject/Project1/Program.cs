using System;

using System;
using System.Runtime.CompilerServices;
using static System.Math;
using MyAlias = System.Collections.Generic;

namespace Project_1
{
    class Program
    {
        static void Main(string[] args)
        {
            Sqrt(9);

            Console.WriteLine("Hello World!");

            MyClass myClass = new();
            myClass.Name = "Something";
            CallDep1();
            CallDep2();
        }

        private static void CallDep1()
        {
            new Dependency1().DoSomething();
        }

        private static float CallDep2()
        {
            Dependency1.DoSomething(2);
            return 7;
        }
    }

    class MyClass
    {
        public String Name { get; set; }
    }
}