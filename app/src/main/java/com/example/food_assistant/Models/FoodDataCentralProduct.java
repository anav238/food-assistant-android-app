package com.example.food_assistant.Models;

import com.example.food_assistant.Enums.ProductType;

import java.util.Map;

public class FoodDataCentralProduct extends Product {

    Map<String, String> nutrientLevels;
    ProductType productType = ProductType.FOOD_DATA_CENTRAL;
    double consumedQuantity;


    @Override
    public String toString() {
        return "OpenFoodFactsProduct{" +
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


    public Map<String, String> getNutrientLevels() {
        return nutrientLevels;
    }

    public void setNutrientLevels(Map<String, String> nutrientLevels) {
        this.nutrientLevels = nutrientLevels;
    }



}
