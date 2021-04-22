package com.example.food_assistant.Models;

import com.example.food_assistant.Enums.Nutrient;

import java.util.List;
import java.util.Map;

public class User {
    String name;
    Map<Nutrient, Double> dailyNutrientConsumption;
    Map<Nutrient, Double> nutrientConsumptionHistory;
    List<String> favoriteProductsBarcodes;
    List<String> historyProductsBarcodes;
    Map<Nutrient, Double> maximumDailyNutrientConsumption;
}
