package com.example.food_assistant.Utils.EventListeners;

import com.example.food_assistant.Models.MealSummary;

public interface MealSummaryFetchListener {
    void onFetchSuccess(MealSummary summary);
    void onFetchNotFound();
    void onFetchFailure(String errorMessage);
}
