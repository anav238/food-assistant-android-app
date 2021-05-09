package com.example.food_assistant.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.food_assistant.Utils.Constants.Nutrients;

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
    List<String> favoriteProductsBarcodes = new ArrayList<>();
    List<String> historyProductsBarcodes = new ArrayList<>();
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
        favoriteProductsBarcodes = in.createStringArrayList();
        historyProductsBarcodes = in.createStringArrayList();
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
                ", favoriteProductsBarcodes=" + favoriteProductsBarcodes +
                ", historyProductsBarcodes=" + historyProductsBarcodes +
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
        return nutrientConsumptionHistory.get(dateString);
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

    public List<String> getFavoriteProductsBarcodes() {
        return favoriteProductsBarcodes;
    }

    public List<String> getHistoryProductsBarcodes() {
        return historyProductsBarcodes;
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

    public void setFavoriteProductsBarcodes(List<String> favoriteProductsBarcodes) {
        this.favoriteProductsBarcodes = favoriteProductsBarcodes;
    }

    public void setHistoryProductsBarcodes(List<String> historyProductsBarcodes) {
        this.historyProductsBarcodes = historyProductsBarcodes;
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
        dest.writeList(favoriteProductsBarcodes);
        dest.writeList(historyProductsBarcodes);
    }
}
