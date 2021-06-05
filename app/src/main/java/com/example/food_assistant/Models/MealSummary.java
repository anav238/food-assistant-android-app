package com.example.food_assistant.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MealSummary implements Serializable {
    private String name;
    private List<IngredientSummary> ingredients = new ArrayList<>();

    public MealSummary(Meal meal) {
        this.name = meal.getName();
        for (Ingredient ingredient:meal.getIngredients())
            ingredients.add(new IngredientSummary(ingredient));
    }

    public MealSummary(String name, List<IngredientSummary> ingredients) {
        this.name = name;
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public List<IngredientSummary> getIngredients() {
        return ingredients;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredients(List<IngredientSummary> ingredients) {
        this.ingredients = ingredients;
    }
}
