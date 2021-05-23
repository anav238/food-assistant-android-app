package com.example.food_assistant.Utils.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_assistant.Models.Product;

import java.util.List;

public class ProductListSharedViewModel extends ViewModel {
    private final MutableLiveData<List<Product>> selected = new MutableLiveData<>();

    public void select(List<Product> products) {
        selected.setValue(products);
    }

    public LiveData<List<Product>> getSelected() {
        return selected;
    }
}
