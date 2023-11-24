using System;

namespace Project_1.Partiality
{
    public partial class Class2
    {
        private string Name;

        protected Class2(string name)
        {
            Name = name;
        }
    }
    
    public partial class Class2
    {
        protected void PrintName()
        {
            Console.WriteLine(Name);
            UnimplementedMethod();
        }
    }
    
    public partial class Class2
    {
        public void SetName(string name)
        {
            if (name is not null)
            {
                Name = name;
            }
        }

        partial void UnimplementedMethod();
    }
}