package com.example.food_assistant.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Meal implements Serializable {
    private String id;
    private String name = "Untitled meal";
    private List<Ingredient> ingredients = new ArrayList<>();
    private Map<String, Double> mealNutrition = new HashMap<>();
    private double totalQuantity = 0.0;

    public Meal() {
        this.id = UUID.randomUUID().toString();
    }

    public Meal(List<Ingredient> ingredients) {
        this.id = UUID.randomUUID().toString();
        for (Ingredient ingredient:ingredients)
            addIngredient(ingredient);
    }

    public Meal(String name, List<Ingredient> ingredients) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        for (Ingredient ingredient:ingredients)
            addIngredient(ingredient);
    }

    public Meal(String id, String name, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        for (Ingredient ingredient:ingredients)
            addIngredient(ingredient);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = new ArrayList<>();
        for (Ingredient ingredient:ingredients)
            addIngredient(ingredient);
    }

    public void addIngredient(Ingredient ingredient) {
        ingredients.add(ingredient);
        totalQuantity += ingredient.getQuantity();

        Map<String, Double> ingredientNutrition = ingredient.getProduct().getNutriments();
        for (String nutrient:ingredientNutrition.keySet()) {
            double nutrientTotalValue = 0.0;
            if (mealNutrition.containsKey(nutrient))
                nutrientTotalValue = mealNutrition.get(nutrient);
            nutrientTotalValue += ingredientNutrition.get(nutrient) * ingredient.getQuantity() / ingredient.getProduct().getBaseQuantity();
            mealNutrition.put(nutrient, nutrientTotalValue);
        }
    }

    public void removeIngredient(Ingredient ingredient) {
        if (ingredients.contains(ingredient)) {
            ingredients.remove(ingredient);
            totalQuantity -= ingredient.getQuantity();

            Map<String, Double> ingredientNutrition = ingredient.getProduct().getNutriments();
            for (String nutrient : ingredientNutrition.keySet()) {
                double nutrientTotalValue = 0.0;
                if (mealNutrition.containsKey(nutrient))
                    nutrientTotalValue = mealNutrition.get(nutrient);
                nutrientTotalValue -= ingredientNutrition.get(nutrient) * ingredient.getQuantity() / ingredient.getProduct().getBaseQuantity();
                mealNutrition.put(nutrient, nutrientTotalValue);
            }
        }
    }

    public Map<String, Double> getMealNutrition() {
        return mealNutrition;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ingredients=" + ingredients +
                ", mealNutrition=" + mealNutrition +
                ", totalQuantity=" + totalQuantity +
                '}';
    }
}
