package com.example.food_assistant.HttpRequest;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.food_assistant.Enums.ProductType;
import com.example.food_assistant.Models.OpenFoodFactsProduct;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Utils.EventListeners.ProductDataFetchListener;
import com.example.food_assistant.Utils.Firebase.ProductDataUtility;
import com.example.food_assistant.Utils.Mappers.ProductMapper;
import com.example.food_assistant.Utils.ViewModels.ProductListSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkManager
{
    private static NetworkManager instance = null;

    public RequestQueue requestQueue;

    private NetworkManager(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized NetworkManager getInstance(Context context) {
        if (null == instance)
            instance = new NetworkManager(context);
        return instance;
    }

    public static synchronized NetworkManager getInstance() {
        if (null == instance) {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public void getProductDetailsByBarcode(String barcode, ProductSharedViewModel productSharedViewModel) {
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseString -> {
                    Log.i("response", responseString);
                    JsonObject responseJson = new Gson().fromJson(responseString, JsonObject.class);
                    if (responseJson.has("product")) {
                        OpenFoodFactsProduct product = ProductMapper.mapOpenFoodFactsProduct(responseJson);
                        product.setId(barcode);
                        product.setProductType(ProductType.OPEN_FOOD_FACTS);
                        productSharedViewModel.select(product);
                        System.out.println(product.toString());
                    }
                    else
                        ProductDataUtility.getProductById(barcode, productSharedViewModel);

                }, error -> Log.i("response", "That didn't work!"));
        requestQueue.add(stringRequest);
    }

    public void getProductDetailsByBarcode(String barcode, ProductDataFetchListener productDataFetchListener) {
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseString -> {
                    Log.i("response", responseString);
                    JsonObject responseJson = new Gson().fromJson(responseString, JsonObject.class);
                    if (responseJson.has("product")) {
                        OpenFoodFactsProduct product = ProductMapper.mapOpenFoodFactsProduct(responseJson);
                        product.setId(barcode);
                        product.setProductType(ProductType.OPEN_FOOD_FACTS);
                        productDataFetchListener.onFetchSuccess(product);
                        System.out.println(product.toString());
                    }
                    else
                        ProductDataUtility.getProductById(barcode, productDataFetchListener);

                }, error -> productDataFetchListener.onFetchFailure(error.getMessage()));
        requestQueue.add(stringRequest);
    }

    public void searchFoodByName(String name, AppCompatActivity activity) {
        String url = "https://api.nal.usda.gov/fdc/v1/foods/search";
        ProductListSharedViewModel productListSharedViewModel = new ViewModelProvider(activity).get(ProductListSharedViewModel.class);

        JsonObject jsonBody = new JsonObject();
        jsonBody.addProperty("query", name);

        JsonArray queryDataTypes = new JsonArray();
        queryDataTypes.add("SR Legacy");
        jsonBody.add("dataType", queryDataTypes);

        jsonBody.addProperty("pageSize", 15);
        jsonBody.addProperty("pageNumber", 1);
        jsonBody.addProperty("sortBy", "dataType.keyword");
        jsonBody.addProperty("sortOrder", "asc");

        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                responseString -> {
                    JsonObject responseJson = new Gson().fromJson(responseString, JsonObject.class);
                    if (responseJson.has("foods")) {
                        JsonArray searchResults = responseJson.get("foods").getAsJsonArray();
                        List<Product> products = new ArrayList<>();
                        for (JsonElement searchResult:searchResults) {
                            Product product = ProductMapper.mapFoodDataCentralProduct(searchResult.getAsJsonObject());
                            products.add(product);
                            System.out.println(product);
                        }
                        productListSharedViewModel.select(products);
                    }
                    else {
                        // No foods found
                    }
                },
                error -> Log.e("VOLLEY", error.toString())) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("X-Api-Key", "cwfnwhmRjkaCVAVCqicCcGas6Rp3gXcoyljdIKhz");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void getProductDetailsByFoodDataCentralId(String productId, ProductDataFetchListener productDataFetchListener) {
        String url = "https://api.nal.usda.gov/fdc/v1/food/" + productId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                responseString -> {
                    JsonObject responseJson = new Gson().fromJson(responseString, JsonObject.class);
                    System.out.println(responseJson);
                    if (responseJson.has("description")) {
                        Product product = ProductMapper.mapFoodDataCentralProduct(responseJson);
                        product.setId(productId);
                        product.setProductType(ProductType.FOOD_DATA_CENTRAL);
                        productDataFetchListener.onFetchSuccess(product);
                        System.out.println(product.toString());
                    }
                    else
                        productDataFetchListener.onFetchNotFound();
                },
                error -> productDataFetchListener.onFetchFailure(error.getMessage())) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("X-Api-Key", "cwfnwhmRjkaCVAVCqicCcGas6Rp3gXcoyljdIKhz");
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }
}