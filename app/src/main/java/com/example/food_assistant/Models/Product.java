package com.example.food_assistant.Models;

import com.example.food_assistant.Enums.ProductType;

import java.io.Serializable;
import java.util.Map;

public class Product implements Serializable {
    String id;
    String productName;
    double baseQuantity;
    ProductType productType = ProductType.CUSTOM;
    Map<String, Double> nutriments;
    String measurementUnit;

    String novaGroup = "Unknown";
    String nutriScoreGrade = "Unknown";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Map<String, Double> getNutriments() {
        return nutriments;
    }

    public void setNutriments(Map<String, Double> nutriments) {
        this.nutriments = nutriments;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public double getBaseQuantity() {
        return baseQuantity;
    }

    public void setBaseQuantity(double baseQuantity) {
        this.baseQuantity = baseQuantity;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public String getNovaGroup() {
        return novaGroup;
    }

    public void setNovaGroup(String novaGroup) {
        this.novaGroup = novaGroup;
    }

    public String getNutriScoreGrade() {
        return nutriScoreGrade;
    }

    public void setNutriScoreGrade(String nutriScoreGrade) {
        this.nutriScoreGrade = nutriScoreGrade;
    }

    @Override
    public String toString() {
        return "OpenFoodFactsProduct{" +
                "productName='" + productName + '\'' +
                ", novaGroup=" + novaGroup +
                ", nutriments=" + nutriments +
                ", nutriScoreGrade='" + nutriScoreGrade + '\'' +
                ", baseQuantity=" + baseQuantity +
                ", measurementUnit='" + measurementUnit + '\'' +
                '}';
    }
}
