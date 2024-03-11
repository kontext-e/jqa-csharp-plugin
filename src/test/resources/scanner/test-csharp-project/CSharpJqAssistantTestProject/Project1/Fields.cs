namespace Project1;

public class Fields
{
    private string privateField;
    public int PublicField;
    internal static Fields StaticField;
    public required bool RequiredField;
    protected volatile float VolatileField;
    protected internal const string DefaultField = "Initial String";
    protected internal const int DefaultInt = 0, DefaultSomething = 3;
}