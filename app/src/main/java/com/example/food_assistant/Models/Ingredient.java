package com.example.food_assistant.Models;

public class Ingredient {
    private Product product;
    private Double quantity;

    public Ingredient(IngredientSummary ingredientSummary) {
        this.product = new Product(ingredientSummary.getProductIdentifier());
        this.quantity = ingredientSummary.getQuantity();
    }

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

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        if (!(object instanceof Ingredient))
            return false;
        Ingredient other = (Ingredient) object;
        return this.product.getId().equals(other.product.getId());
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "product=" + product +
                ", quantity=" + quantity +
                '}';
    }
}
