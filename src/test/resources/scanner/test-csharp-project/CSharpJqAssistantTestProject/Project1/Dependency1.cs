using System;
using Project1.TypeDependencies;
using MyAlias = System.Collections.Generic;

namespace Project1
{
    internal class Dependency1
    {
        internal static Dependency1 DoSomething(float t) { return null; }
        
        internal static void DoSomething(Properties dep)
        {
            String[] strings = { "Hi", "Hello" };
            DoSomething(strings);
        }

        internal static void DoSomething(string[] strings) { }
    }

    internal static class MethodOverloads
    {
        internal static void DoSomething(Properties dep)
        {
            Double d = 5.8;
            DoSomething(d);
        }

        internal static void DoSomething(double strings) { }
    }
}