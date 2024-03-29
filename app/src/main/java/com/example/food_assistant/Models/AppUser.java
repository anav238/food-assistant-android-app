package com.example.food_assistant.Models;

import com.example.food_assistant.Utils.Nutrition.NutrientCalculator;
import com.example.food_assistant.Utils.Nutrition.Nutrients;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUser {

    String name = "";
    String email = "";
    Map<String, Map<String, Double>> nutrientConsumptionHistory = new HashMap<>();

    List<ProductIdentifier> productFavorites = new ArrayList<>();
    List<ProductIdentifier> productHistory = new ArrayList<>();

    List<MealIdentifier> mealFavorites = new ArrayList<>();
    List<MealIdentifier> mealHistory = new ArrayList<>();

    Map<String, Double> maximumNutrientDV = new HashMap<>();

    public AppUser() {
        maximumNutrientDV.putAll(Nutrients.nutrientDefaultDV);

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String dateString = formatter.format(date);
        Map<String, Double> todayNutrientConsumption = new HashMap<>();
        for (String nutrient:Nutrients.nutrientDefaultDV.keySet())
            todayNutrientConsumption.put(nutrient, 0.0);
        nutrientConsumptionHistory.put(dateString, todayNutrientConsumption);
    }

    public AppUser(String name, String email) {
        this.name = name;
        this.email = email;
        maximumNutrientDV.putAll(Nutrients.nutrientDefaultDV);

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String dateString = formatter.format(date);
        Map<String, Double> todayNutrientConsumption = new HashMap<>();
        for (String nutrient:Nutrients.nutrientDefaultDV.keySet())
            todayNutrientConsumption.put(nutrient, 0.0);
        nutrientConsumptionHistory.put(dateString, todayNutrientConsumption);
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", nutrientConsumptionHistory=" + nutrientConsumptionHistory +
                ", favoriteProductsBarcodes=" + productFavorites +
                ", historyProductsBarcodes=" + productHistory +
                ", maximumDailyNutrientConsumption=" + maximumNutrientDV +
                '}';
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Double> getTodayNutrientConsumption() {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String dateString = formatter.format(date);
        if (nutrientConsumptionHistory.containsKey(dateString))
            return nutrientConsumptionHistory.get(dateString);

        Map<String, Double> todayNutrientConsumption = new HashMap<>();
        for (String nutrient:Nutrients.nutrientDefaultDV.keySet())
            todayNutrientConsumption.put(nutrient, 0.0);
        nutrientConsumptionHistory.put(dateString, todayNutrientConsumption);
        return todayNutrientConsumption;
    }

    public void updateUserNutrientConsumptionWithProduct(Product product, Double productQuantity) {
        Map<String, Double> todayNutrientConsumption = this.getTodayNutrientConsumption();
        Map<String, Double> productNutrients = product.getNutriments();
        Double productBaseQuantity = product.getBaseQuantity();

        for (String nutrient:todayNutrientConsumption.keySet()) {
            if (productNutrients.containsKey(nutrient)) {
                double newConsumption = todayNutrientConsumption.get(nutrient) + productNutrients.get(nutrient) * (productQuantity / productBaseQuantity);
                todayNutrientConsumption.put(nutrient, newConsumption);
            }
        }
        this.updateTodayNutrientConsumption(todayNutrientConsumption);

        ProductIdentifier productIdentifier = new ProductIdentifier(product.getId(), product.getProductName(), product.getProductType());
        if (productHistory.contains(productIdentifier))
            productHistory.remove(productIdentifier);

        productHistory.add(0, productIdentifier);
        if (productHistory.size() > 30)
            productHistory.remove(productHistory.size() - 1);
        System.out.println(productHistory);
    }

    public void updateUserNutrientConsumptionWithMeal(Meal meal, Double mealQuantity) {
        Map<String, Double> todayNutrientConsumption = this.getTodayNutrientConsumption();
        todayNutrientConsumption = NutrientCalculator.addMealNutritionToUserDailyNutrition(todayNutrientConsumption, meal, mealQuantity);
        this.updateTodayNutrientConsumption(todayNutrientConsumption);

        for (Ingredient ingredient:meal.getIngredients())
           saveProductToHistory(ingredient.getProduct());

        saveMealToHistory(meal);
    }

    public void updateTodayNutrientConsumption(Map<String, Double> newNutrientConsumption) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String dateString = formatter.format(date);
        nutrientConsumptionHistory.put(dateString, newNutrientConsumption);
    }

    private void saveProductToHistory(Product product) {
        ProductIdentifier productIdentifier = new ProductIdentifier(product.getId(), product.getProductName(), product.getProductType());
        if (productHistory.contains(productIdentifier))
            productHistory.remove(productIdentifier);

        productHistory.add(0, productIdentifier);
        if (productHistory.size() > 30)
            productHistory.remove(productHistory.size() - 1);
    }

    public void saveMealToHistory(Meal meal) {
        MealIdentifier mealIdentifier = new MealIdentifier(meal.getId(), meal.getName());
        if (mealHistory.contains(mealIdentifier))
            mealHistory.remove(mealIdentifier);
        mealHistory.add(0, mealIdentifier);
        if (mealHistory.size() > 30)
            mealHistory.remove(mealHistory.size() - 1);
    }

    public Map<String, Map<String, Double>> getNutrientConsumptionHistory() {
        return nutrientConsumptionHistory;
    }

    public List<ProductIdentifier> getProductFavorites() {
        return productFavorites;
    }

    public void addProductFavorite(Product product) {
        productFavorites.add(new ProductIdentifier(product.getId(), product.getProductName(), product.getProductType()));
    }

    public void addProductFavorite(ProductIdentifier productIdentifier) {
        productFavorites.add(productIdentifier);
    }

    public void removeProductFromFavorites(ProductIdentifier productIdentifier) {
        productFavorites.remove(productIdentifier);
    }

    public void addMealFavorite(Meal meal) {
        mealFavorites.add(new MealIdentifier(meal.getId(), meal.getName()));
    }

    public void addMealFavorite(MealIdentifier mealIdentifier) {
        mealFavorites.add(mealIdentifier);
    }

    public void removeMealFromFavorites(MealIdentifier mealIdentifier) {
        mealFavorites.remove(mealIdentifier);
    }


    public List<ProductIdentifier> getProductHistory() {
        return productHistory;
    }

    public void removeProductFromHistory(ProductIdentifier productIdentifier) {
        productHistory.remove(productIdentifier);
    }

    public Map<String, Double> getMaximumNutrientDV() {
        return maximumNutrientDV;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNutrientConsumptionHistory(Map<String, Map<String, Double>> nutrientConsumptionHistory) {
        this.nutrientConsumptionHistory = nutrientConsumptionHistory;
    }

    public void setMaximumNutrientDV(Map<String, Double> maximumNutrientDV) {
        this.maximumNutrientDV = maximumNutrientDV;
    }

    public void setProductFavorites(List<ProductIdentifier> productFavorites) {
        this.productFavorites = productFavorites;
    }

    public void setProductHistory(List<ProductIdentifier> productHistory) {
        this.productHistory = productHistory;
    }

    public List<MealIdentifier> getMealFavorites() {
        return mealFavorites;
    }

    public void setMealFavorites(List<MealIdentifier> mealFavorites) {
        this.mealFavorites = mealFavorites;
    }

    public List<MealIdentifier> getMealHistory() {
        return mealHistory;
    }

    public void setMealHistory(List<MealIdentifier> mealHistory) {
        this.mealHistory = mealHistory;
    }
}
