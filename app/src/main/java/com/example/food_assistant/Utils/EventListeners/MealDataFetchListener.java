package com.example.food_assistant.Utils.EventListeners;

import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.Product;

public interface MealDataFetchListener {
    void onFetchSuccess(Meal meal);
    void onFetchNotFound();
    void onFetchFailure(String errorMessage);
}
