package com.example.food_assistant.Utils.EventListeners;

import com.example.food_assistant.Models.Product;

public interface ProductDataFetchListener {
    void onFetchSuccess(Product product);
    void onFetchNotFound();
    void onFetchFailure(String errorMessage);
}
