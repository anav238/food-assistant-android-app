package com.example.food_assistant.Utils.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_assistant.Models.AppUser;

public class UserSharedViewModel extends ViewModel {
    private final MutableLiveData<AppUser> selected = new MutableLiveData<AppUser>();

    public void select(AppUser user) {
        selected.setValue(user);
    }

    public LiveData<AppUser> getSelected() {
        return selected;
    }
}
