package com.example.food_assistant.Utils.Firebase;

import android.util.Log;

import com.example.food_assistant.Models.User;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDataLogger {
    private static DatabaseReference mDatabase;

    public static void logUserData(FirebaseUser user) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        String userId = user.getUid();
        mDatabase.child("users").child(userId).get().addOnCompleteListener(task -> {
            Log.i("userData", task.getResult().toString());
            if (!task.isSuccessful() || task.getResult().getValue() == null) {
                User dbUser = new User(user.getDisplayName(), user.getEmail());
                mDatabase.child("users").child(userId).setValue(dbUser);
                Log.i("TEST - NEW USER ADDED", user.toString());
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
            }
        });
    }
}
