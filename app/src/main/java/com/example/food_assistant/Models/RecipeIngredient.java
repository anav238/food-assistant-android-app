package com.example.food_assistant.Models;

public class RecipeIngredient {
    private int quantity;
    private String measurementUnit;
    private Product ingredient;

    public RecipeIngredient(int quantity, String measurementUnit, Product ingredient) {
        this.quantity = quantity;
        this.measurementUnit = measurementUnit;
        this.ingredient = ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public Product getIngredient() {
        return ingredient;
    }

    public void setIngredient(Product ingredient) {
        this.ingredient = ingredient;
    }
}
