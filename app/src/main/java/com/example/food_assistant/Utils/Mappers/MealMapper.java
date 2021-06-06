package com.example.food_assistant.Utils.Mappers;

import com.example.food_assistant.Enums.ProductType;
import com.example.food_assistant.Models.Ingredient;
import com.example.food_assistant.Models.IngredientSummary;
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.MealIdentifier;
import com.example.food_assistant.Models.MealSummary;
import com.example.food_assistant.Models.Product;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MealMapper {
    public static MealSummary mapMealSummary(JsonObject json) {
        Gson gson = new Gson();
        MealSummary summary = new MealSummary();
        if (json.has("name"))
            summary.setName(json.get("id").getAsString());
        if (json.has("ingredients")) {
            JsonArray ingredientsJson = json.get("ingredients").getAsJsonArray();
            List<IngredientSummary> ingredients = new ArrayList<>();
            for (JsonElement mealElement:ingredientsJson) {
                IngredientSummary ingredientSummary = gson.fromJson(mealElement.getAsJsonObject(), IngredientSummary.class);
                ingredients.add(ingredientSummary);
            }
            summary.setIngredients(ingredients);
        }

        return summary;
    }

    public static Meal mapMinimalMealDataFromSummary(MealSummary mealSummary) {
        Meal meal = new Meal();
        meal.setId(mealSummary.getId());
        meal.setName(mealSummary.getName());
        List<Ingredient> minimalIngredientData = new ArrayList<>();
        for (IngredientSummary ingredientSummary:mealSummary.getIngredients()) {
            Ingredient ingredient = new Ingredient(ingredientSummary);
            minimalIngredientData.add(ingredient);
        }
        meal.setIngredients(minimalIngredientData);
        return meal;
    }
}
