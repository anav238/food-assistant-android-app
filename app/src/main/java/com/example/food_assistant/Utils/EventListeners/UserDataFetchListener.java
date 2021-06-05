package com.example.food_assistant.Utils.EventListeners;

import com.example.food_assistant.Models.AppUser;

public interface UserDataFetchListener {
    void onFetchSuccess(AppUser appUser);
    void onFetchNotFound();
    void onFetchFailure(String errorMessage);
}
