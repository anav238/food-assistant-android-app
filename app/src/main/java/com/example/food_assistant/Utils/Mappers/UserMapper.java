package com.example.food_assistant.Utils.Mappers;

import android.util.Log;

import com.example.food_assistant.Models.AppUser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
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
        Log.i("TEST", user.toString());
        return user;
    }
}
