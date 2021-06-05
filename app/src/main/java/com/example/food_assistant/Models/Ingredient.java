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

    @Override
    public boolean equals(Object object) {
        if (object == this)
            return true;
        if (!(object instanceof Ingredient))
            return false;
        Ingredient other = (Ingredient) object;
        return this.quantity.equals(other.quantity) && this.product.getId().equals(other.product.getId()) && this.product.getProductType() == other.product.getProductType();
    }


}
