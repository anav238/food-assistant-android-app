package com.example.food_assistant.Utils.Firebase;

import android.util.Log;

import com.example.food_assistant.Enums.ProductType;
import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.MealIdentifier;
import com.example.food_assistant.Models.MealSummary;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Models.ProductIdentifier;
import com.example.food_assistant.Utils.EventListeners.MealSummaryFetchListener;
import com.example.food_assistant.Utils.Mappers.MealMapper;
import com.example.food_assistant.Utils.Mappers.ProductMapper;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class MealDataUtility {
    private static DatabaseReference mDatabase;
    private static NetworkManager networkManager = NetworkManager.getInstance();

    public static void logMealData(Meal meal) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        MealSummary mealSummary = new MealSummary(meal);
        mDatabase.child("meals").child(meal.getId()).setValue(mealSummary);
    }

    public static boolean[] determineIfProductsAreFavoriteForUser(List<MealIdentifier> mealIdentifiers, AppUser appUser) {
        boolean[] areFavorites = new boolean[mealIdentifiers.size()];
        List<MealIdentifier> mealFavorites = appUser.getMealFavorites();
        int i = 0;
        for (MealIdentifier mealIdentifier:mealIdentifiers) {
            if (mealFavorites.contains(mealIdentifier))
                areFavorites[i] = true;
            else
                areFavorites[i] = false;
            i++;
        }
        return areFavorites;
    }

    public static void getMealSummaryById(String id, MealSummaryFetchListener mealSummaryFetchListener) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        mDatabase.child("meals").child(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().getValue() != null) {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                Gson gson = new Gson();
                JsonElement element = gson.toJsonTree(task.getResult().getValue());
                JsonObject object = (JsonObject) element;
                MealSummary mealSummary = MealMapper.mapMealSummary(object);
                mealSummary.setId(id);
                mealSummaryFetchListener.onFetchSuccess(mealSummary);
            }
        }).addOnFailureListener(error -> {});
    }
}
