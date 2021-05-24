package com.example.food_assistant.Utils.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_assistant.Models.Recipe;

public class RecipeSharedViewModel extends ViewModel {
    private final MutableLiveData<Recipe> selected = new MutableLiveData<>();

    public void select(Recipe recipe) {
        selected.setValue(recipe);
    }

    public LiveData<Recipe> getSelected() {
        return selected;
    }
}
