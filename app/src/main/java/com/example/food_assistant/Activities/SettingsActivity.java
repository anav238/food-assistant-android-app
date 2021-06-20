package com.example.food_assistant.Activities;

import android.os.Bundle;

import com.example.food_assistant.Models.AppDataManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.Nutrition.Nutrients;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;


public class SettingsActivity extends AppCompatActivity {

    private UserSharedViewModel userSharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar myChildToolbar =
                findViewById(R.id.toolbar);
        setSupportActionBar(myChildToolbar);

        ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);

        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        updateUserData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //updateUserData();
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
        AppDataManager.initialize(new AppUser());
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> finish());
    }

    private void populateNutrientValues() {
        AppUser user = AppDataManager.getInstance().getAppUser();
        if (user != null) {
            Map<String, Double> userNutrientValues = user.getMaximumNutrientDV();
            for (String nutrient:userNutrientValues.keySet()) {
                String editTextId = "editText_" + nutrient.replace("-", "_") + "_quantity";
                System.out.println(nutrient);
                EditText nutrientEditText = findViewById(getResources().getIdentifier(editTextId, "id", getPackageName()));
                nutrientEditText.setText(String.valueOf(userNutrientValues.get(nutrient)));
            }
        }
    }

    public void saveChanges(View view) {
        AppUser user = AppDataManager.getInstance().getAppUser();
        if (user != null) {
            Map<String, Double> userNutrientValues = user.getMaximumNutrientDV();
            for (String nutrient:userNutrientValues.keySet()) {
                String editTextId = "editText_" + nutrient.replace("-", "_") + "_quantity";
                EditText nutrientEditText = findViewById(getResources().getIdentifier(editTextId, "id", getPackageName()));
                try {
                    Double nutrientQuantity = Double.parseDouble(nutrientEditText.getText().toString());
                    userNutrientValues.put(nutrient, nutrientQuantity);
                }
                catch (NumberFormatException e) {
                    nutrientEditText.setError("Please enter a valid quantity!");
                    break;
                }
            }
            user.setMaximumNutrientDV(userNutrientValues);
        }
        userSharedViewModel.select(user);
        AppDataManager.getInstance().setAppUser(user);
        UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);
        Toast toast = Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void resetToDefaults(View view) {
        AppUser user = AppDataManager.getInstance().getAppUser();
        if (user != null) {
            user.setMaximumNutrientDV(Nutrients.nutrientDefaultDV);
            populateNutrientValues();
        }
    }

    private void updateUserData() {
        AppUser user = AppDataManager.getInstance().getAppUser();
        System.out.println(user);
        if (user != null) {
            String name = user.getName();
            TextView usernameTextView = findViewById(R.id.usernameTextView);
            usernameTextView.setText(name);
            populateNutrientValues();
        }
    }

}
