package com.example.food_assistant.Utils.EventListeners;

import com.example.food_assistant.Models.Product;

public interface ProductDataFetchListener {
    public void onFetchSuccess(Product product);
    public void onFetchNotFound();
    public void onFetchFailure(String errorMessage);
}
