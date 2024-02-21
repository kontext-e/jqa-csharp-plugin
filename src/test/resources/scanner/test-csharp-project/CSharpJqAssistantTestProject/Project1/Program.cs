using System;

using System;
using System.Runtime.CompilerServices;
using Project1.ExtensionMethods;
using static System.Math;
using MyAlias = System.Collections.Generic;

namespace Project1
{
    class Program
    {
        static void Main(string[] args)
        {
            
        }

        private static void CallDep1()
        {
            new Dependency1().DoNothing();
        }

        private static float CallDep2()
        {
            Dependency1.DoSomething(2);
            return 7;
        }
    }
}