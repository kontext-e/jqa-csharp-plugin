using System;

namespace Project1;

public class Constructors(double dx, double dy)
{
    public double Magnitude => Math.Sqrt(dx * dx + dy * dy);
    public double Direction => Math.Atan2(dy, dx);

    public Constructors() : this(0,0) { }
    
}

public class ClassWithDefaultConstructor { }
public static class ClassWithoutAnyConstructor { }


public record Test
{
    private string _field;
    private int Property { get; set; }
}