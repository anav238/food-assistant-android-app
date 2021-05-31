package com.example.food_assistant.Utils.Nutrition;

import android.os.Bundle;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Ingredient;
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.Product;

import java.util.HashMap;
import java.util.Map;

public class NutrientCalculator {

    public static Map<String, Double> computeNutritionValuesSum(Map<String, Double> firstNutritionTable, Map<String, Double> secondNutritionTable) {
        Map<String, Double> nutritionSum = new HashMap<>();

        for (String nutrient:firstNutritionTable.keySet()) {
            double totalNutrientValue = firstNutritionTable.get(nutrient);
            if (secondNutritionTable.containsKey(nutrient))
                totalNutrientValue += secondNutritionTable.get(nutrient);
            nutritionSum.put(nutrient, totalNutrientValue);
        }

        for (String nutrient:secondNutritionTable.keySet()) {
            if (!firstNutritionTable.containsKey(nutrient))
                nutritionSum.put(nutrient, secondNutritionTable.get(nutrient));
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
