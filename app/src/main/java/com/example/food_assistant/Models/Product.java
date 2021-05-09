package com.example.food_assistant.Models;

import java.util.Map;

public class Product {
    String productName = "Unknown";
    String novaGroup = "Unknown";
    Map<String, String> nutrientLevels;
    Map<String, Double> nutriments;
    String nutriScoreGrade = "Unknown";

    double baseQuantity;
    double consumedQuantity;
    String measurementUnit;

    @Override
    public String toString() {
        return "Product{" +
                "productName='" + productName + '\'' +
                ", novaGroup=" + novaGroup +
                ", nutrientLevels=" + nutrientLevels +
                ", nutriments=" + nutriments +
                ", nutriScoreGrade='" + nutriScoreGrade + '\'' +
                ", baseQuantity=" + baseQuantity +
                ", consumedQuantity=" + consumedQuantity +
                ", measurementUnit='" + measurementUnit + '\'' +
                '}';
    }

    public double getBaseQuantity() {
        return baseQuantity;
    }

    public void setBaseQuantity(double baseQuantity) {
        this.baseQuantity = baseQuantity;
    }

    public double getConsumedQuantity() {
        return consumedQuantity;
    }

    public void setConsumedQuantity(double consumedQuantity) {
        this.consumedQuantity = consumedQuantity;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getNovaGroup() {
        return novaGroup;
    }

    public void setNovaGroup(String novaGroup) {
        this.novaGroup = novaGroup;
    }

    public Map<String, String> getNutrientLevels() {
        return nutrientLevels;
    }

    public void setNutrientLevels(Map<String, String> nutrientLevels) {
        this.nutrientLevels = nutrientLevels;
    }

    public Map<String, Double> getNutriments() {
        return nutriments;
    }

    public void setNutriments(Map<String, Double> nutriments) {
        this.nutriments = nutriments;
    }

    public String getNutriScoreGrade() {
        return nutriScoreGrade;
    }

    public void setNutriScoreGrade(String nutriScoreGrade) {
        this.nutriScoreGrade = nutriScoreGrade;
    }
}
