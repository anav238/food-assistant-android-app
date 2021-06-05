package com.example.food_assistant.Utils.Mappers;

import android.util.Log;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.MealIdentifier;
import com.example.food_assistant.Models.ProductIdentifier;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMapper {
    public static AppUser map(JsonObject userJson) {
        Gson gson = new Gson();
        AppUser user = new AppUser();

        if (userJson.has("name"))
            user.setName(userJson.get("name").getAsString());

        if (userJson.has("email"))
            user.setEmail(userJson.get("email").getAsString());

        if (userJson.has("maximumNutrientDV")) {
            Map<String, Double> maximumNutrientDVMap = gson.fromJson(userJson.get("maximumNutrientDV").getAsJsonObject(), HashMap.class);
            user.setMaximumNutrientDV(maximumNutrientDVMap);
        }

        if (userJson.has("nutrientConsumptionHistory")) {
            Map<String, Map<String, Double>> nutrientConsumptionHistoryMap = gson.fromJson(userJson.get("nutrientConsumptionHistory").getAsJsonObject(), HashMap.class);
            user.setNutrientConsumptionHistory(nutrientConsumptionHistoryMap);
        }

        if (userJson.has("productHistory")) {
            JsonArray productHistoryJson = userJson.get("productHistory").getAsJsonArray();
            List<ProductIdentifier> productHistory = new ArrayList<>();
            for (JsonElement productElement:productHistoryJson) {
                ProductIdentifier productIdentifier = gson.fromJson(productElement.getAsJsonObject(), ProductIdentifier.class);
                productHistory.add(productIdentifier);
            }
            user.setProductHistory(productHistory);
        }

        if (userJson.has("productFavorites")) {
            JsonArray productFavoritesJson = userJson.get("productFavorites").getAsJsonArray();
            List<ProductIdentifier> productFavorites = new ArrayList<>();
            for (JsonElement productElement:productFavoritesJson) {
                ProductIdentifier productIdentifier = gson.fromJson(productElement.getAsJsonObject(), ProductIdentifier.class);
                productFavorites.add(productIdentifier);
            }
            user.setProductFavorites(productFavorites);
        }

        if (userJson.has("mealHistory")) {
            JsonArray mealHistoryJson = userJson.get("mealHistory").getAsJsonArray();
            List<MealIdentifier> mealHistory = new ArrayList<>();
            for (JsonElement mealElement:mealHistoryJson) {
                MealIdentifier mealIdentifier = gson.fromJson(mealElement.getAsJsonObject(), MealIdentifier.class);
                mealHistory.add(mealIdentifier);
            }
            user.setMealHistory(mealHistory);
        }

        if (userJson.has("mealFavorites")) {
            JsonArray mealFavoritesJson = userJson.get("mealFavorites").getAsJsonArray();
            List<MealIdentifier> mealFavorites = new ArrayList<>();
            for (JsonElement mealElement:mealFavoritesJson) {
                MealIdentifier mealIdentifier = gson.fromJson(mealElement.getAsJsonObject(), MealIdentifier.class);
                mealFavorites.add(mealIdentifier);
            }
            user.setMealFavorites(mealFavorites);
        }

        Log.i("TEST", user.toString());
        return user;
    }
}
