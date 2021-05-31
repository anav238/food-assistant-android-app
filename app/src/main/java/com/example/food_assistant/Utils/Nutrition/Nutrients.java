package com.example.food_assistant.Utils.Nutrition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Nutrients {
    public static Map<String, Double> nutrientDefaultDV = new HashMap<String, Double>() {{
        put("energy-kcal", 2000.0);
        put("fat", 78.0);
        put("saturated-fat", 20.0);
        put("carbohydrates", 300.0);
        put("fiber", 38.0);
        put("sugars", 50.0);
        put("proteins", 50.0);
        put("salt", 2.4);
    }};

    public static List<String> badNutrients = new ArrayList<String>() {{
        add("energy-kcal");
        add("saturated-fat");
        add("sugars");
        add("salt");
    }};

    public static Map<String, String> foodDataCentralNutrientMapping = new HashMap<String, String>() {{
        put("Fatty acids, total saturated", "saturated-fat");
        put("Protein", "proteins");
        put("Total lipid (fat)", "fat");
        put("Energy", "energy-kcal");
        put("Sugars, total including NLEA", "sugars");
        put("Carbohydrate, by difference", "carbohydrates");
        put("Sodium, Na", "salt");
        put("Fiber, total dietary", "fiber");
    }};
}
