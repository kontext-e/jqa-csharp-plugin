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

            MyClass myClass = new("a random name");
            MyClass yourClass = myClass;
            String yourname;
            var something = myClass.Name;
            yourname = yourClass.Name;
            if (something == myClass.Name)
            {
                myClass.doSomethingAgain();
            }
            myClass.ID = "ID";
            myClass.doSomethingAgain();
            CallDep1();
            CallDep2();
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

    class MyClass
    {
        public String Name { get; }
        public String ID { get; set; }

        public MyClass(String name)
        {
            Name = name;
        }

        public void doSomethingAgain()
        {
            Console.WriteLine();
        }
    }
}