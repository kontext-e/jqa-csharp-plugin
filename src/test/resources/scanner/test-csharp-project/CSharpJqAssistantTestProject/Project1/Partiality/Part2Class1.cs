namespace Project_1.Partiality
{
    public partial class Class1
    {
        public string ReturnNull()
        {
            PartialMethod();
            return null;
        }

        private partial int PartialMethod();
    }
}