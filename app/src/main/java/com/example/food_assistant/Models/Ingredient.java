package com.example.food_assistant.Models;

public class Ingredient {
    private final Product product;
    private final Double quantity;

    public Ingredient(Product product, Double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public Double getQuantity() {
        return quantity;
    }

}
