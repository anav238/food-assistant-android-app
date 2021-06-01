package com.example.food_assistant.Models;

import androidx.annotation.Nullable;

import com.example.food_assistant.Enums.ProductType;

public class ProductIdentifier {
    String id;
    String productName;
    ProductType productType;

    public ProductIdentifier() {
    }

    public ProductIdentifier(String id, String productName, ProductType productType) {
        this.id = id;
        this.productName = productName;
        this.productType = productType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @Override
    public String toString() {
        return "ProductIdentifier{" +
                "id='" + id + '\'' +
                ", productName='" + productName + '\'' +
                ", productType=" + productType +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProductIdentifier productIdentifier = (ProductIdentifier) obj;
        return this.id.equals(productIdentifier.id) && this.productName.equals(productIdentifier.productName) && this.productType == productIdentifier.productType;
    }
}
