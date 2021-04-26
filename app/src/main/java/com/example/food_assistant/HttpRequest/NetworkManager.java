package com.example.food_assistant.HttpRequest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Utils.Mappers.ProductMapper;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String responseString) {
                        Log.i("response", responseString.substring(0,500));
                        JsonObject responseJson = new Gson().fromJson(responseString, JsonObject.class);
                        Product product = ProductMapper.map(responseJson);
                        ProductSharedViewModel productSharedViewModel = new ViewModelProvider(activity).get(ProductSharedViewModel.class);
                        productSharedViewModel.select(product);
                        System.out.println(product.toString());

                        SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
                        FragmentManager fragmentManager = activity.getSupportFragmentManager();
                        selectProductQuantityFragment.show(fragmentManager, "test");
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
}