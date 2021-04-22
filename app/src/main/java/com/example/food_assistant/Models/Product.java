package com.example.food_assistant.Models;

import com.example.food_assistant.Enums.Nutrient;
import com.example.food_assistant.Enums.NutrientLevel;

import java.util.Map;

public class Product {
    String product_name;
    int nova_group;
    Map<Nutrient, NutrientLevel> nutrientLevels;
    Map<String, Double> nutriments;
    int nutriscore_score;
}
