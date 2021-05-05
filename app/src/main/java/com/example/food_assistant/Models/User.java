package com.example.food_assistant.Models;

import com.example.food_assistant.Enums.Nutrient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    String name = "";
    String email = "";
    Map<String, Double> dailyNutrientConsumption;
    Map<String, Double> nutrientConsumptionHistory;
    List<String> favoriteProductsBarcodes;
    List<String> historyProductsBarcodes;
    Map<String, Double> maximumDailyNutrientConsumption;

    public User() {
        maximumDailyNutrientConsumption.put("calories", 2000.0);
        maximumDailyNutrientConsumption.put("sugars", 2000.0);
        maximumDailyNutrientConsumption.put("fats", 2000.0);
        maximumDailyNutrientConsumption.put("saturated_fats", 2000.0);
        maximumDailyNutrientConsumption.put("carbs", 2000.0);
        maximumDailyNutrientConsumption.put("salts", 2000.0);
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        maximumDailyNutrientConsumption = new HashMap<>();
        maximumDailyNutrientConsumption.put("calories", 2000.0);
        maximumDailyNutrientConsumption.put("sugars", 2000.0);
        maximumDailyNutrientConsumption.put("fats", 2000.0);
        maximumDailyNutrientConsumption.put("saturated_fats", 2000.0);
        maximumDailyNutrientConsumption.put("carbs", 2000.0);
        maximumDailyNutrientConsumption.put("salts", 2000.0);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", dailyNutrientConsumption=" + dailyNutrientConsumption +
                ", nutrientConsumptionHistory=" + nutrientConsumptionHistory +
                ", favoriteProductsBarcodes=" + favoriteProductsBarcodes +
                ", historyProductsBarcodes=" + historyProductsBarcodes +
                ", maximumDailyNutrientConsumption=" + maximumDailyNutrientConsumption +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Double> getDailyNutrientConsumption() {
        return dailyNutrientConsumption;
    }

    public Map<String, Double> getNutrientConsumptionHistory() {
        return nutrientConsumptionHistory;
    }

    public List<String> getFavoriteProductsBarcodes() {
        return favoriteProductsBarcodes;
    }

    public List<String> getHistoryProductsBarcodes() {
        return historyProductsBarcodes;
    }

    public Map<String, Double> getMaximumDailyNutrientConsumption() {
        return maximumDailyNutrientConsumption;
    }
}
