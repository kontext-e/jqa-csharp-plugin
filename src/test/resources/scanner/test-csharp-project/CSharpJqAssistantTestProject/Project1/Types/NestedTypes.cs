namespace Project1.Types;

public class NestedTypes
{
    protected class NestedClass<T> where T : Properties
    {
        private Fields field;
        protected T bar { get; set; }
    }
    
    public enum NestedEnum
    {
        A, B, C
    }
}