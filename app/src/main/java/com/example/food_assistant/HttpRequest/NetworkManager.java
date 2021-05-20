package com.example.food_assistant.HttpRequest;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.food_assistant.Enums.ProductType;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.Models.OpenFoodFactsProduct;
import com.example.food_assistant.Utils.Firebase.ProductDataUtility;
import com.example.food_assistant.Utils.Mappers.ProductMapper;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NetworkManager
{
    private static final String TAG = "NetworkManager";
    private static NetworkManager instance = null;

    private static final String prefixURL = "http://some/url/prefix/";

    //for Volley API
    public RequestQueue requestQueue;

    private NetworkManager(Context context)
    {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        //other stuf if you need
    }

    public static synchronized NetworkManager getInstance(Context context)
    {
        if (null == instance)
            instance = new NetworkManager(context);
        return instance;
    }

    //this is so you don't need to pass context each time
    public static synchronized NetworkManager getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(NetworkManager.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public void getProductDetailsByBarcode(String barcode, AppCompatActivity activity) {
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
        ProductSharedViewModel productSharedViewModel = new ViewModelProvider(activity).get(ProductSharedViewModel.class);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseString) {
                        Log.i("response", responseString);
                        JsonObject responseJson = new Gson().fromJson(responseString, JsonObject.class);
                        if (responseJson.has("product")) {
                            OpenFoodFactsProduct product = ProductMapper.mapOpenFoodFactsProduct(responseJson);
                            product.setId(barcode);
                            //product.setProductType(ProductType.OPEN_FOOD_FACTS);
                            productSharedViewModel.select(product);
                            System.out.println(product.toString());

                            SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            selectProductQuantityFragment.show(fragmentManager, "test");
                        }
                        else
                            ProductDataUtility.getProductById(barcode, activity);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("response", "That didn't work!");
            }
        });

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    public void searchFoodByName(String name, AppCompatActivity activity) {
        String url = "https://api.nal.usda.gov/fdc/v1/foods/search";

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

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Log.e("APICALL", "\n response: " + response),
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

        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }
}