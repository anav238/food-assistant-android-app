package com.example.food_assistant.Models;

public class IngredientSummary {
    private final ProductIdentifier productIdentifier;
    private final Double quantity;

    public IngredientSummary(Ingredient ingredient) {
        this.productIdentifier = new ProductIdentifier(ingredient.getProduct().getId(), ingredient.getProduct().getProductName(), ingredient.getProduct().getProductType());
        this.quantity = ingredient.getQuantity();
    }

    public IngredientSummary(ProductIdentifier productIdentifier, Double quantity) {
        this.productIdentifier = productIdentifier;
        this.quantity = quantity;
    }

    public ProductIdentifier getProductIdentifier() {
        return productIdentifier;
    }

    public Double getQuantity() {
        return quantity;
    }
}
