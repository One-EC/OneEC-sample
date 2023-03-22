
using Newtonsoft.Json;
using Oneec_Sample.models;
using Oneec_Sample.services;
using System;

namespace Oneec_Sample
{
    class Program
    {
        static void Main(string[] args)
        {
            //  Initialize settings
            OneECAPI oneECAPI = new OneECAPI();
            ////  Get orders
            //oneECAPI.GetOrderList();

            //// Get the products
            //oneECAPI.GetProducts();

            ////  Create or modify product
            //oneECAPI.SaveProduct();

            //// Get the combination products
            //oneECAPI.GetMerchantCombinationProductList();

            ////  Create or modify combination products
            //oneECAPI.SaveProductCombinations();

            //// Get the ships
            //oneECAPI.GetShips();

        }
    }
}
