package com.example.food_assistant.Utils.Firebase;

import android.util.Log;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Utils.Mappers.UserMapper;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class UserDataUtility {
    private static DatabaseReference mDatabase;

    public static void logUserData(FirebaseUser user, UserSharedViewModel userSharedViewModel) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        String userId = user.getUid();
        mDatabase.child("users").child(userId).get().addOnCompleteListener(task -> {
            Log.i("userData", task.getResult().toString());
            if (!task.isSuccessful() || task.getResult().getValue() == null) {
                AppUser appUser = new AppUser(user.getDisplayName(), user.getEmail());
                mDatabase.child("users").child(userId).setValue(appUser);
                userSharedViewModel.select(appUser);
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                Gson gson = new Gson();
                JsonElement userElement = gson.toJsonTree(task.getResult().getValue());
                JsonObject userObject = (JsonObject) userElement;
                AppUser appUser = UserMapper.map(userObject);
                appUser.setName(user.getDisplayName());
                appUser.setEmail(user.getEmail());
                userSharedViewModel.select(appUser);
            }
        });
    }

    public static void updateUserDataToDb(FirebaseUser user, UserSharedViewModel userSharedViewModel) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        AppUser loggedUser = userSharedViewModel.getSelected().getValue();
        if (loggedUser != null) {
            Log.i("Logging user data to DB", loggedUser.toString());
            mDatabase.child("users").child(user.getUid()).setValue(loggedUser);
        }
    }
}
