package com.example.food_assistant.Utils.Constants;

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
}
