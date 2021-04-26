package com.example.food_assistant.Utils.Mappers;

import com.example.food_assistant.Models.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class ProductMapper {
    public static Product map(JsonObject productJson) {
        System.out.println(productJson);
        Gson gson = new Gson();
        Product product = new Product();
        if (productJson.has("product")) {
            productJson = productJson.getAsJsonObject("product");
            if (productJson.has("product_name"))
                product.setProductName(productJson.get("product_name").getAsString());

            if (productJson.has("nova_group"))
                product.setNovaGroup(productJson.get("nova_group").getAsString());

            if (productJson.has("nutriscore_grade"))
                product.setNutriScoreGrade(productJson.get("nutriscore_grade").getAsString().toUpperCase());

            if (productJson.has("nutriments")) {
                System.out.println(productJson.get("nutriments"));
                product.setNutriments(gson.fromJson(productJson.get("nutriments").getAsJsonObject(), HashMap.class));
            }
            if (productJson.has("nutrient_levels"))
                product.setNutrientLevels(gson.fromJson(productJson.get("nutrient_levels").getAsJsonObject(), HashMap.class));

            if (productJson.has("product_quantity"))
                product.setBaseQuantity(productJson.get("product_quantity").getAsDouble());

            if (productJson.has("quantity")) {
                String measurementUnit = productJson.get("quantity").getAsString().split("\\s+")[0];
                product.setMeasurementUnit(measurementUnit);
            }
        }
        return product;
    }
}
