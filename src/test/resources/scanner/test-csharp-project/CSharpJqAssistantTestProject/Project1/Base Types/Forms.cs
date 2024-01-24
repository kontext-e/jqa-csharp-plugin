using System;
using System.Collections.Generic;

namespace Project_1.Base_Types
{
    public abstract class Form {
    }

    public abstract class Rectangle: Form {
    }

    public class Circle: Form
    {
        private Person _person = new("William", "Thimm", 23);
        public required string Address { get; set; }
        public required string PhoneNumber;

        private void Method()
        {
            _person.SayName();
        }
    }

    public struct Coordinates
    {
        public double X { get; private set; }

        public double Y { get; private set; }

        public Coordinates(double x, double y)
        {
            (X, Y) = (x, y);
        }

        public void MoveTo(double x, double y)
        {
            (X, Y) = (x, y);
        }
    }

    public record Person(string Firstname, string Lastname, int Age)
    {
        private List<string> _favouriteFood = new();
        private int BodyCount { get; set; }

        public Person(string Firstname, string lastName, int Age, int bodyCount) : this(Firstname, lastName, Age)
        {
            BodyCount = bodyCount;
        }
        
        public void SayName()
        {
            Console.WriteLine("Hello my Name is " + Firstname + Lastname);
        }
    }

    public record Pet(string Name, int Age);
    
    public enum Accessor{
        Private, Public
    }

    public record struct Animal()
    {

    }
    
}