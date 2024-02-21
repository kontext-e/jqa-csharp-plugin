using System;
using MyAlias = System.Collections.Generic;

namespace Project1
{
    internal class Dependency1
    {
        internal void DoNothing()
        {
            
        }
        
        internal static Dependency1 DoSomething(float? t)
        {
            return null;
        }
        
        internal static void DoSomething(Dependency1? dep)
        {
            String[] strings = { "Hi", "Hello" };
            DoSomething(strings);
        }

        private static void DoSomething(string[]? strings)
        {
            
        }
    }
}