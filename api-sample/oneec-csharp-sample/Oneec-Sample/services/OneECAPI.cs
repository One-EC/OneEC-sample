using Newtonsoft.Json;
using NPOI.POIFS.Crypt;
using Oneec_Sample.models;
using System;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Security.Cryptography;
using System.Text;
using System.Web;

namespace Oneec_Sample.services

{
    class OneECAPI
    {
        // dev url
        const string _endpoint = "https://dev-api.oneec.ai";

        /* Please contact the official counterpart */
        // partner key id
        string _partnerKeyId = "";
        // aes key 
        string _AESKey = "";
        // aes iv
        string _AESIV = "";
        // hash key
        string _hashKey = "";
        // merchant account token
        string _merchantAccountToken = "";
        public OneECAPI()
        {
        }

        private void SetHeader(HttpClient httpClient, string apiRoute, string body)
        {
            var authorization = $"{_partnerKeyId}.{_merchantAccountToken}";
            var target = apiRoute + body + _hashKey;
            var xSign = CryptService.SHA256Hash(target);
            httpClient.DefaultRequestHeaders.Add("Authorization", $"Bearer {authorization}");
            httpClient.DefaultRequestHeaders.Add("X-sign", xSign);
            httpClient.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
        }

        /// <summary>
        /// Get orders
        /// </summary>
        public void GetOrderList()
        {
            try
            {
                HttpClient httpClient = new HttpClient();
                string apiRoute = "/oapi/v1/data/merchant/orders";

                string param = "";
                //param = param+"?";
                //param = param + "&shipStartDate=2022-03-16T16:46:05.005Z";      // ship start date  default "" 
                //param = param + "&shipEndDate=2023-03-16T16:46:05.005Z";        // ship end date  default "" 
                //param = param + "&orderCreateDate=2022-03-16T16:46:05.005Z";    // order create date  default ""  
                //param = param + "&orderStatus=";                                // 
                //param = param + "&channelId=AyNAVo";                            // channel id
                //param = param + "&channelSettingId=partner_unit_test";          // platform
                //param = param + "&start=0";                                     // start default 0
                //param = param + "&limit=20";                                    // page size default 10
                string body = "";
                var endpoint = $"{_endpoint}{apiRoute}{param}";
                SetHeader(httpClient, apiRoute + param, body);
                var message = httpClient.GetAsync(endpoint).Result;
                string response = message.Content.ReadAsStringAsync().Result;
                Console.WriteLine(response);

                var objectModel = JsonConvert.DeserializeObject<Response<String>>(response);
                if (objectModel.status == 200)
                {
                    var data = CryptService.AesGcmDecryptTByBase64(_AESKey, _AESIV, objectModel.data);
                    Console.WriteLine(data);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }

        }

        /// <summary>
        /// Get the products
        /// </summary>
        public void GetProducts()
        {
            try
            {
                HttpClient httpClient = new HttpClient();
                string apiRoute = "/oapi/v1/data/merchant/products";
                string param = "";
                //param = param+"?";
                //param = param + "&limit=2";      // page size default 10   ,   max=100
                //param = param + "&start=9";      // start default 0
                //param = param + "&itemNumber=";  // product's number
                string body = "";
                var endpoint = $"{_endpoint}{apiRoute}{param}";
                SetHeader(httpClient, apiRoute + param, body);

                var message = httpClient.GetAsync(endpoint).Result;
                string resultStr = message.Content.ReadAsStringAsync().Result;
                Console.WriteLine(resultStr);
                var objectModel = JsonConvert.DeserializeObject<Response<MerchantProductData>>(resultStr);

            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }
        }

        /// <summary>
        /// Get the combination products
        /// </summary>
        public void GetMerchantCombinationProductList()
        {
            try
            {
                HttpClient httpClient = new HttpClient();
                string apiRoute = "/oapi/v1/data/merchant/product_combinations";
                string param = "";
                //param = param+"?";
                //param = param + "&limit=2";      // page size default 10   ,   max=100
                //param = param + "&start=9";      // start default 0
                //param = param + "&itemNumber=";  // product's number
                string body = "";
                var endpoint = $"{_endpoint}{apiRoute}{param}";
                SetHeader(httpClient, apiRoute+ param, body);

                var message = httpClient.GetAsync(endpoint).Result;
                string resultStr = message.Content.ReadAsStringAsync().Result;
                Console.WriteLine(resultStr);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }

        }

        /// <summary>
        /// Create or modify product
        /// </summary>
        public void SaveProduct()
        {
            try
            {
                HttpClient httpClient = new HttpClient();
                string apiRoute = "/oapi/v1/data/merchant/products/save";
                var specs = new MerchantProductSpec[1];
                specs[0] = new MerchantProductSpec() { };
                var payload = new MerchantProduct
                {
                    productName = "TBSProduct",     
                    brand = "The Body Shop 美體小舖",                    
                    supplier = "美雅國際",              
                    condition = 0,
                    goodType = 1,
                    distributionTemperature = 0,
                    isFragile = false,
                    specs = specs,
                    suggestPrice = 500,
                    normalPrice = 400,
                    cost = 300,
                    qty = 50,
                    isCanOvertakeOrder = true,
                    itemNumber = "TBS12345",
                    insertDt = "2022-05-05T22:23:11.333Z",
                    modifiedDt = "2022-05-05T22:23:11.333Z",
                    size = new MerchantProductSize() { height = 1, length = 1, weight = 2, width = 5 },
                };
                var body = JsonConvert.SerializeObject(payload);
                var endpoint = $"{_endpoint}{apiRoute}";
                SetHeader(httpClient, apiRoute, body);

                Console.WriteLine(body);
                var httpContent = new StringContent(body, Encoding.UTF8, "application/json");
                var message = httpClient.PostAsync(endpoint, httpContent).Result;
                string resultStr = message.Content.ReadAsStringAsync().Result;
                Console.WriteLine(resultStr);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }

        }

        /// <summary>
        /// Create or modify combination products
        /// </summary>
        public void SaveProductCombinations()
        {
            try
            {
                HttpClient httpClient = new HttpClient();
                string apiRoute = "/oapi/v1/data/merchant/product_combinations/save";
                var data = new MerchantCombinationProduct();
                data.itemNumber = "1234533";
                data.addCombinationInfo(new MerchantCombinationInfo() { itemNumber = "070105022", cost = 33, price = 3, productName = "TBS-A", qty = 1 });
                data.addCombinationInfo(new MerchantCombinationInfo() { itemNumber = "0696204", cost = 333, price = 6, productName = "TBS-B", qty = 2 });
                data.modifiedDt = "2022-05-05T22:23:11.333Z";
                data.insertDt = "2022-05-05T22:23:11.333Z";
                var body = JsonConvert.SerializeObject(data);
                var endpoint = $"{_endpoint}{apiRoute}";
                SetHeader(httpClient, apiRoute, body);

                Console.WriteLine(body);
                var httpContent = new StringContent(body, Encoding.UTF8, "application/json");
                var message = httpClient.PostAsync(endpoint, httpContent).Result;
                string resultStr = message.Content.ReadAsStringAsync().Result;
                Console.WriteLine(resultStr);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }
        }
        
        public void GetChannels()
        {
            try
            {
                HttpClient httpClient = new HttpClient();
                string apiRoute = "/oapi/v1/data/merchant/channels/actived";
                string param = "";
                Console.WriteLine(param);
                string body = "";
                var endpoint = $"{_endpoint}{apiRoute}{param}";
                SetHeader(httpClient, apiRoute + param, body);
                var message = httpClient.GetAsync(endpoint).Result;
                string response = message.Content.ReadAsStringAsync().Result;
                Console.WriteLine(response);

                var objectModel = JsonConvert.DeserializeObject<Response<String>>(response);
                if (objectModel.status == 200)
                {
                    //var data = CryptService.AesGcmDecryptTByBase64(_AESKey, _AESIV, objectModel.data);
                    //Console.WriteLine(data);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
            }
        }
    }
}
