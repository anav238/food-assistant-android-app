package com.example.food_assistant.Utils.Nutrition;

import android.os.Bundle;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Ingredient;
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.Product;

import java.util.HashMap;
import java.util.Map;

public class NutrientCalculator {

    public static Map<String, Double> addProductNutritionToUserDailyNutrition(Map<String, Double> userNutrition, Map<String, Double> productNutrition, double productQuantity) {
        Map<String, Double> nutritionSum = new HashMap<>();

        for (String nutrient:userNutrition.keySet()) {
            double totalNutrientValue = userNutrition.get(nutrient);
            if (productNutrition.containsKey(nutrient))
                totalNutrientValue += productNutrition.get(nutrient) * productQuantity / 100.0;
            nutritionSum.put(nutrient, totalNutrientValue);
        }

        for (String nutrient:productNutrition.keySet()) {
            if (!userNutrition.containsKey(nutrient))
                nutritionSum.put(nutrient, productNutrition.get(nutrient) * productQuantity / 100);
        }

        return nutritionSum;
    }

    public static Map<String, Double> getNutrientsPercentageFromMaximumDV(Map<String, Double> nutrients, AppUser appUser) {
        Map<String, Double> maxNutrientDVs = appUser.getMaximumNutrientDV();
        Map<String, Double> nutrientPercentages = new HashMap<>();
        for (String nutrient:maxNutrientDVs.keySet()) {
            double nutrientPercentage = 0.0;
            if (nutrients.containsKey(nutrient))
                nutrientPercentage = (int) (nutrients.get(nutrient) * 100 / maxNutrientDVs.get(nutrient));
            nutrientPercentages.put(nutrient, nutrientPercentage);
        }
        return nutrientPercentages;
    }

    public static Map<String, Double> getMealNutritionPerServing(Meal meal, double servingSize) {
        Map<String, Double> totalMealNutrition = meal.getMealNutrition();;
        Map<String, Double> mealNutritionPerServing = new HashMap<>();

        for (String nutrient:totalMealNutrition.keySet()) {
            double nutrientQuantity = totalMealNutrition.get(nutrient);
            double nutrientQuantityPerServing = servingSize * nutrientQuantity / meal.getTotalQuantity();
            mealNutritionPerServing.put(nutrient, nutrientQuantityPerServing);
        }
        return mealNutritionPerServing;
    }
}
