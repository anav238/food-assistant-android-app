package com.example.food_assistant.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.food_assistant.Utils.Nutrition.Nutrients;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppUser implements Parcelable {
    String name = "";
    String email = "";
    Map<String, Map<String, Double>> nutrientConsumptionHistory = new HashMap<>();
    List<String> favoritesIds = new ArrayList<>();
    List<String> historyIds = new ArrayList<>();
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

    protected AppUser(Parcel in) {
        name = in.readString();
        email = in.readString();
        favoritesIds = in.createStringArrayList();
        historyIds = in.createStringArrayList();
    }

    public static final Creator<AppUser> CREATOR = new Creator<AppUser>() {
        @Override
        public AppUser createFromParcel(Parcel in) {
            return new AppUser(in);
        }

        @Override
        public AppUser[] newArray(int size) {
            return new AppUser[size];
        }
    };

    @Override
    public String toString() {
        return "AppUser{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", nutrientConsumptionHistory=" + nutrientConsumptionHistory +
                ", favoriteProductsBarcodes=" + favoritesIds +
                ", historyProductsBarcodes=" + historyIds +
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

    public void updateUserNutrientConsumption(Product product, Double productQuantity) {
        Map<String, Double> todayNutrientConsumption = this.getTodayNutrientConsumption();
        Map<String, Double> productNutrients = product.getNutriments();
        Double productBaseQuantity = product.getBaseQuantity();

        for (String nutrient:todayNutrientConsumption.keySet()) {
            String productNutrientKey = nutrient + "_value";
            if (productNutrients.containsKey(productNutrientKey)) {
                double newConsumption = todayNutrientConsumption.get(nutrient) + productNutrients.get(productNutrientKey) * (productQuantity / productBaseQuantity);
                todayNutrientConsumption.put(nutrient, newConsumption);
            }
        }
        this.updateTodayNutrientConsumption(todayNutrientConsumption);

        List<String> historyIds = this.getHistoryIds();
        historyIds.add(product.getId());
        this.setHistoryIds(historyIds);
    }

    public void updateTodayNutrientConsumption(Map<String, Double> newNutrientConsumption) {
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String dateString = formatter.format(date);
        nutrientConsumptionHistory.put(dateString, newNutrientConsumption);
    }

    public Map<String, Map<String, Double>> getNutrientConsumptionHistory() {
        return nutrientConsumptionHistory;
    }

    public List<String> getFavoritesIds() {
        return favoritesIds;
    }

    public List<String> getHistoryIds() {
        return historyIds;
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

    public void setFavoritesIds(List<String> favoritesIds) {
        this.favoritesIds = favoritesIds;
    }

    public void setHistoryIds(List<String> historyIds) {
        this.historyIds = historyIds;
    }

    public void setMaximumNutrientDV(Map<String, Double> maximumNutrientDV) {
        this.maximumNutrientDV = maximumNutrientDV;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.email);
        dest.writeMap(this.nutrientConsumptionHistory);
        dest.writeMap(this.maximumNutrientDV);
        dest.writeList(favoritesIds);
        dest.writeList(historyIds);
    }
}
