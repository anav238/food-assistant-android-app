package com.example.food_assistant.Utils.Mappers;

import com.example.food_assistant.Enums.ProductType;
import com.example.food_assistant.Models.OpenFoodFactsProduct;
import com.example.food_assistant.Models.Product;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;

public class ProductMapper {
    public static OpenFoodFactsProduct mapOpenFoodFactsProduct(JsonObject productJson) {
        System.out.println(productJson);
        Gson gson = new Gson();
        OpenFoodFactsProduct product = new OpenFoodFactsProduct();
        if (productJson.has("product")) {
            productJson = productJson.getAsJsonObject("product");

            if (productJson.has("product_name_ro") && productJson.get("product_name_ro").getAsString().length() > 0)
                product.setProductName(productJson.get("product_name_ro").getAsString());
            else if (productJson.has("product_name_en") && productJson.get("product_name_en").getAsString().length() > 0)
                product.setProductName(productJson.get("product_name_en").getAsString());
            else if (productJson.has("product_name") && productJson.get("product_name").getAsString().length() > 0)
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
                String measurementUnit = productJson.get("quantity").getAsString().split("\\s+")[1];
                product.setMeasurementUnit(measurementUnit);
            }
        }
        return product;
    }

    public static Product mapFirebaseProduct(JsonObject productJson) {
        System.out.println(productJson);
        Gson gson = new Gson();
        Product product = new Product();
        productJson = productJson.getAsJsonObject("product");
        if (productJson.has("id"))
           product.setProductName(productJson.get("id").getAsString());
        if (productJson.has("productName"))
            product.setProductName(productJson.get("productName").getAsString());
        if (productJson.has("nutriments"))
            product.setNutriments(gson.fromJson(productJson.get("nutriments").getAsJsonObject(), HashMap.class));
        if (productJson.has("productType")) {
            String productTypeString = productJson.get("productType").getAsString();
            if (productTypeString.equals("CUSTOM"))
                product.setProductType(ProductType.CUSTOM);
            else if (productTypeString.equals("OPEN_FOOD_FACTS"))
                product.setProductType(ProductType.OPEN_FOOD_FACTS);
            else if (productTypeString.equals("FOOD_DATA_CENTRAL"))
                product.setProductType(ProductType.FOOD_DATA_CENTRAL);
        }
        return product;
    }
}
