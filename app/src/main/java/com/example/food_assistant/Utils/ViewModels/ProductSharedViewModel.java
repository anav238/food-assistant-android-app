package com.example.food_assistant.Utils.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_assistant.Models.Product;

public class ProductSharedViewModel extends ViewModel {
    private final MutableLiveData<Product> selected = new MutableLiveData<Product>();

    public void select(Product product) {
        selected.setValue(product);
    }

    public LiveData<Product> getSelected() {
        return selected;
    }
}
