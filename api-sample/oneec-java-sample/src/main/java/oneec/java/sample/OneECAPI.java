package oneec.java.sample;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import oneec.java.sample.helper.CryptHelper;
import oneec.java.sample.models.*;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class OneECAPI {
    private static final String _domain = "https://dev-api.oneec.ai"; // Dev url
    private static final String _partnerKeyID = ""; // Please contact the official counterpart
    private static final String _aesKey = ""; // Please contact the official counterpart
    private static final String _aesIv = ""; // Please contact the official counterpart
    private static final String _hashKey = ""; // Please contact the official counterpart
    private static final String _merchantAccessToken = ""; // Please contact the official counterpart
    private static CryptHelper _cryptHelper;

    private static String getXSign(String apiUrl, String body) throws Exception {
        String sign = apiUrl + body + _hashKey;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(sign.getBytes("utf8"));
        return String.format("%064x", new BigInteger(1, digest.digest()));
    }

    public static void main(String[] args) {
        _cryptHelper = new CryptHelper(_aesKey, _aesIv, "AES/GCM/NoPadding");

        try {
            getOrderList();
            getProductList();
            getMerchantCombinationProductList();
            saveProduct();
            saveProductCombinations();
            getChannels();
            getShips();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the order list
     */
    public static void getOrderList() {
        try {
            String apiRoute = "/oapi/v1/data/merchant/orders";

            String param = "";
//            param = param+"?";
//            param = param + "&shipStartDate=2022-03-16T16:46:05.005Z";      // ship start date  default ""
//            param = param + "&shipEndDate=2023-03-16T16:46:05.005Z";        // ship end date  default ""
//            param = param + "&orderCreateDate=2022-03-16T16:46:05.005Z";    // order create date  default ""
//            param = param + "&orderStatus=";                                //
//            param = param + "&channelId=AyNAVo";                            // channel id
//            param = param + "&channelSettingId=partner_unit_test";          // platform
//            param = param + "&start=0";                                     // start default 0
//            param = param + "&limit=20";                                    // page size default 10

            String endpoint = _domain + apiRoute + param;

            String body = "";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(_partnerKeyID + "." + _merchantAccessToken);
            headers.add("X-sign", getXSign(apiRoute + param, body));

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> entity = new RestTemplate().exchange(endpoint, HttpMethod.GET, request, String.class);

            TypeReference<CipherResponse> typeReference = new TypeReference<>() {
            };

            CipherResponse response = new JsonMapper().readValue(entity.getBody(), typeReference);
            if (response.status == HttpStatus.OK.value()) {
                String decryptedData = _cryptHelper.decryptFromBase64(response.data);
                System.out.println("decryptedData: " + decryptedData);
            } else {
                System.out.println("response: " + new JsonMapper().writeValueAsString(response));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ", " + e);
        }
    }

    /**
     * Get the product list
     */
    public static void getProductList() {
        try {
            String apiRoute = "/oapi/v1/data/merchant/products";

            String param = "";
            //param = param+"?";
            //param = param + "&limit=2";      // page size default 10
            //param = param + "&start=9";      // start default 0
            //param = param + "&itemNumber=";  // product's number

            String endpoint = _domain + apiRoute + param;

            String body = "";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(_partnerKeyID + "." + _merchantAccessToken);
            headers.add("X-sign", getXSign(apiRoute + param, body));

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> entity = new RestTemplate().exchange(endpoint, HttpMethod.GET, request, String.class);

            TypeReference<Response> typeReference = new TypeReference<>() {
            };

            Response response = new JsonMapper().readValue(entity.getBody(), typeReference);
            System.out.println(response.data);

            if (response.status == HttpStatus.OK.value()) {
                System.out.println("data: " + response.data);
            } else {
                System.out.println("response: " + new JsonMapper().writeValueAsString(response));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ", " + e);
        }
    }

    /**
     * Get the combination product list
     */
    public static void getMerchantCombinationProductList() {
        try {
            String apiRoute = "/oapi/v1/data/merchant/product_combinations";

            String endpoint = _domain + apiRoute;

            String param = "";
            //param = param+"?";
            //param = param + "&limit=2";      // page size default 10
            //param = param + "&start=9";      // start default 0
            //param = param + "&itemNumber=";  // product's number

            String body = "";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(_partnerKeyID + "." + _merchantAccessToken);
            headers.add("X-sign", getXSign(apiRoute + param, body));

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> entity = new RestTemplate().exchange(endpoint, HttpMethod.GET, request, String.class);

            TypeReference<Response> typeReference = new TypeReference<>() {
            };

            Response response = new JsonMapper().readValue(entity.getBody(), typeReference);
            System.out.println(response.data);

            if (response.status == HttpStatus.OK.value()) {
                System.out.println("data: " + response.data);
            } else {
                System.out.println("response: " + new JsonMapper().writeValueAsString(response));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ", " + e);
        }
    }

    /**
     * Create or modify product
     */
    public static void saveProduct() {
        try {
            String apiRoute = "/oapi/v1/data/merchant/products/save";

            String endpoint = _domain + apiRoute;

            MerchantProduct merchantProduct = new MerchantProduct();
            merchantProduct.productName = "TBSProduct";
            merchantProduct.brand = "The Body Shop 美體小舖";
            merchantProduct.supplier = "美雅國際";
            merchantProduct.condition = 0;
            merchantProduct.goodType = 1;
            merchantProduct.distributionTemperature = 0;
            merchantProduct.isFragile = false;
            merchantProduct.specs = new ArrayList<>();
            merchantProduct.suggestPrice = BigDecimal.valueOf(500);
            merchantProduct.normalPrice = BigDecimal.valueOf(400);
            merchantProduct.cost = BigDecimal.valueOf(300);
            merchantProduct.qty = 50;
            merchantProduct.isCanOvertakeOrder = true;
            merchantProduct.itemNumber = "TBS12345";
            merchantProduct.insertDt = "2022-05-05T22:23:11.333Z";
            merchantProduct.modifiedDt = "2022-05-05T22:23:11.333Z";

            MerchantProduct.MerchantProductSize size = new MerchantProduct.MerchantProductSize();
            size.height = 1;
            size.length = 1;
            size.weight = 1;
            size.width = 1;

            merchantProduct.size = size;
            merchantProduct.specs = new ArrayList<>();

            String body = new JsonMapper().writeValueAsString(merchantProduct);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(_partnerKeyID + "." + _merchantAccessToken);
            headers.add("X-sign", getXSign(apiRoute, body));

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> entity = new RestTemplate().exchange(endpoint, HttpMethod.POST, request, String.class);

            TypeReference<Response> typeReference = new TypeReference<>() {
            };

            Response response = new JsonMapper().readValue(entity.getBody(), typeReference);
            System.out.println(response.data);

            if (response.status == HttpStatus.OK.value()) {
                System.out.println("data: " + response.data);
            } else {
                System.out.println("response: " + new JsonMapper().writeValueAsString(response));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ", " + e);
        }
    }

    /**
     * Create or modify combination products
     */
    public static void saveProductCombinations() {
        try {
            String apiRoute = "/oapi/v1/data/merchant/product_combinations/save";

            String endpoint = _domain + apiRoute;

            MerchantCombinationProduct merchantCombinationProduct = new MerchantCombinationProduct();
            merchantCombinationProduct.itemNumber = "1234533";
            merchantCombinationProduct.modifiedDt = "2022-05-12T14:52:11.333Z";
            merchantCombinationProduct.insertDt = "2022-05-12T14:52:11.333Z";

            List<MerchantCombinationInfo> combinationInfoList = new ArrayList<>();

            MerchantCombinationInfo combinationInfoI01 = new MerchantCombinationInfo();
            combinationInfoI01.itemNumber = "070105022";
            combinationInfoI01.productName = "TBS-A";
            combinationInfoI01.qty = 1;
            combinationInfoI01.cost = BigDecimal.valueOf(33);
            combinationInfoI01.price = BigDecimal.valueOf(3);
            combinationInfoList.add(combinationInfoI01);

            MerchantCombinationInfo combinationInfoI02 = new MerchantCombinationInfo();
            combinationInfoI02.itemNumber = "0696204";
            combinationInfoI02.productName = "TBS-B";
            combinationInfoI02.qty = 2;
            combinationInfoI02.cost = BigDecimal.valueOf(333);
            combinationInfoI02.price = BigDecimal.valueOf(6);
            combinationInfoList.add(combinationInfoI02);
            merchantCombinationProduct.combinationInfos = combinationInfoList;

            String body = new JsonMapper().writeValueAsString(merchantCombinationProduct);
            System.out.println(body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(_partnerKeyID + "." + _merchantAccessToken);
            headers.add("X-sign", getXSign(apiRoute, body));

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> entity = new RestTemplate().exchange(endpoint, HttpMethod.POST, request, String.class);

            TypeReference<Response> typeReference = new TypeReference<>() {
            };

            Response response = new JsonMapper().readValue(entity.getBody(), typeReference);
            System.out.println(response.data);

            if (response.status == HttpStatus.OK.value()) {
                System.out.println("data: " + response.data);
            } else {
                System.out.println("response: " + new JsonMapper().writeValueAsString(response));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ", " + e);
        }
    }

    public static void getChannels() {
        try {
            String apiRoute = "/oapi/v1/data/merchant/channels/actived";
            String param = "";
            String endpoint = _domain + apiRoute;
            System.out.println("endpoint: " + endpoint);

            String body = "";
            System.out.println("body: " + body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(_partnerKeyID + "." + _merchantAccessToken);
            headers.add("X-sign", getXSign(apiRoute + param, body));

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> entity = new RestTemplate().exchange(endpoint, HttpMethod.GET, request, String.class);

            TypeReference<Response> typeReference = new TypeReference<>() {
            };

            Response response = new JsonMapper().readValue(entity.getBody(), typeReference);
            System.out.println(response.data);

            if (response.status == HttpStatus.OK.value()) {
                System.out.println("data: " + response.data);
            } else {
                System.out.println("response: " + new JsonMapper().writeValueAsString(response));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ", " + e);
        }
    }

    public static void getShips() {
        try {
            String apiRoute = "/oapi/v1/data/merchant/ships";
            String param = "?";
            param = param + "limit=100"; // page size default 50
            param = param + "&start=0"; // start default 0
//            param = param + "&orderCreateDate="; // order create date default ""
//            param = param + "&orderSns=OM220829000001,OM220829000002,OM220829000003"; // order sn default ""

            String endpoint = _domain + apiRoute + param;
            System.out.println("endpoint: " + endpoint);

            String body = "";
            System.out.println("body: " + body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(_partnerKeyID + "." + _merchantAccessToken);
            headers.add("X-sign", getXSign(apiRoute + param, body));

            HttpEntity<String> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> entity = new RestTemplate().exchange(endpoint, HttpMethod.GET, request, String.class);

            TypeReference<CipherResponse> typeReference = new TypeReference<>() {
            };

            CipherResponse response = new JsonMapper().readValue(entity.getBody(), typeReference);
            System.out.println("response data: " + response.data);

            if (response.status == HttpStatus.OK.value()) {
                String decryptedData = _cryptHelper.decryptFromBase64(response.data);
                System.out.println("decryptedData: " + decryptedData);
            } else {
                System.out.println("response: " + new JsonMapper().writeValueAsString(response));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage() + ", " + e);
        }
    }
}
