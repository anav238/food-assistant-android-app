package com.example.food_assistant.Models;

import com.example.food_assistant.Enums.ProductType;

import java.util.Map;

public class OpenFoodFactsProduct extends Product {

    Map<String, String> nutrientLevels;
    ProductType productType = ProductType.OPEN_FOOD_FACTS;
    double consumedQuantity;

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
