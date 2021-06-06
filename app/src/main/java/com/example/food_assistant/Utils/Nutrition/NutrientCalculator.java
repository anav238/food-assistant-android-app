package com.example.food_assistant.Utils.Nutrition;

import android.util.Log;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Meal;

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

    public static Map<String, Double> addMealNutritionToUserDailyNutrition(Map<String, Double> userNutrition, Meal meal, Double mealConsumedQuantity) {
        if (meal.getTotalQuantity() == 0.0)
            return userNutrition;

        Map<String, Double> mealNutrition = meal.getMealNutrition();
        Map<String, Double> nutritionSum = new HashMap<>();
        Log.i("INFO", "User nutrition: " + userNutrition.toString());
        Log.i("INFO", "Meal nutrition: " + mealNutrition.toString());

        for (String nutrient:userNutrition.keySet()) {
            double totalNutrientValue = userNutrition.get(nutrient);
            if (mealNutrition.containsKey(nutrient))
                totalNutrientValue += mealNutrition.get(nutrient) * mealConsumedQuantity / meal.getTotalQuantity();
            nutritionSum.put(nutrient, totalNutrientValue);
        }

        for (String nutrient:mealNutrition.keySet()) {
            if (!userNutrition.containsKey(nutrient))
                nutritionSum.put(nutrient, mealNutrition.get(nutrient));
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
