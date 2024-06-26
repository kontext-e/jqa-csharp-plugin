class Alpha { }
class Beta { }
class Service
{
    public void Process<T, TS>(T item, TS item2) { }
}

class Foo
{
    static void Bar(string[] args)
    {
        var a = new Alpha();
        var b = new Beta();

        var service = new Service();
        service.Process(a, b); // Same as "service.Process<Alpha>(a)"

        var objects = new object[] { a, b };
        foreach (var o in objects)
        {
        }
    }
}
