package com.example.food_assistant.Utils.Firebase;

import android.util.Log;

import com.example.food_assistant.Enums.ProductType;
import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.MealSummary;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Utils.Mappers.ProductMapper;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MealDataUtility {
    private static DatabaseReference mDatabase;
    private static NetworkManager networkManager = NetworkManager.getInstance();

    public static void logMealData(Meal meal) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        MealSummary mealSummary = new MealSummary(meal);
        mDatabase.child("meals").child(meal.getId()).setValue(mealSummary);
    }

    /*public static void getProductById(String productId, ProductSharedViewModel productSharedViewModel) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        mDatabase.child("products").child(productId).get().addOnCompleteListener(task -> {
            Log.i("productData", task.getResult().toString());
            if (!task.isSuccessful() || task.getResult().getValue() == null) {
                Product product = new Product();
                product.setId(productId);
                product.setProductType(ProductType.CUSTOM);
                productSharedViewModel.select(product);
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                Gson gson = new Gson();
                JsonElement productElement = gson.toJsonTree(task.getResult().getValue());
                JsonObject productObject = (JsonObject) productElement;
                Product product = ProductMapper.mapFirebaseProduct(productObject);
                product.setProductType(ProductType.CUSTOM);
                productSharedViewModel.select(product);
            }
        }).addOnFailureListener(error -> {});
    }*/
}
