using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Project_1
{
    internal class Properties
    {

        public string Property1 { get; set; }

        private protected int Property9 {get; set;}

        public string Property2 { get; private set; }

        public string Property3 => Property1 + Property2;
        private string Property4 => Property1 + Property2;

        public string Property5
        {
            get { return Property1; }
            set => Property2 = value;
        }

        int Property6 { get; init; }

        private string Property7 {  get; set; }
        public static string Property8 { get; set; }

        int Property10 { get; set; }

    }
}