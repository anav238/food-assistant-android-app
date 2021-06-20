package com.example.food_assistant.Models;

public class AppDataManager {

    private static AppDataManager instance;
    private AppUser appUser;

    private AppDataManager(AppUser newUser) {
        appUser = newUser;
    }

    public synchronized static AppDataManager getInstance() {
        if (instance == null)
            throw new NullPointerException("Please call initialize() before getting the instance.");
        return instance;
    }

    public synchronized static void initialize(AppUser appUser) {
        if (appUser == null)
            throw new NullPointerException("Provided user is null");
        else if (instance == null) {
            instance = new AppDataManager(appUser);
        }
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser newUser) {appUser = newUser; }

}
