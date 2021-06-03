package com.example.food_assistant.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.R;
import com.example.food_assistant.Fragments.SettingsFragment;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.Nutrition.Nutrients;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Map;


public class SettingsActivity extends AppCompatActivity {

    private UserSharedViewModel userSharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingsFragmentContainer, new SettingsFragment())
                .commit();

        Toolbar myChildToolbar =
                findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();

            TextView usernameTextView = findViewById(R.id.usernameTextView);
            usernameTextView.setText(name);

            UserDataUtility.getUserData(user, userSharedViewModel);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateUserPreferences();
    }

    private void updateUserPreferences() {
        SharedPreferences sharedPref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            AppUser appUser = userSharedViewModel.getSelected().getValue();
            Map<String, Double> currentUserMaxDVs = appUser.getMaximumNutrientDV();
            for (String nutrient : Nutrients.nutrientDefaultDV.keySet()) {
                String nutrientPreferenceId = nutrient + "_max";
                System.out.println(nutrientPreferenceId);
                String nutrientValueString = sharedPref.getString(nutrientPreferenceId, "");
                double nutrientValue = 0.0;
                try {
                    nutrientValue = Double.parseDouble(nutrientValueString);
                } catch (NumberFormatException e) {
                    nutrientValue = Nutrients.nutrientDefaultDV.get(nutrient);
                }
                currentUserMaxDVs.put(nutrient, (double) nutrientValue);
            }
            appUser.setMaximumNutrientDV(currentUserMaxDVs);
            UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void logoutUser(View view) {
        updateUserPreferences();
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> finish());
    }

}
