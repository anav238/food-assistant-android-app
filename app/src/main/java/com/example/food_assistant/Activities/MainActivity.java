package com.example.food_assistant.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Models.AppDataManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.EventListeners.UserDataFetchListener;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserDataFetchListener {

    private static final int RC_SIGN_IN = 123;
    private AppDataManager appDataManager;
    private UserSharedViewModel userSharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);
        NavigationUI.setupWithNavController(bottomNav, navController);

        NetworkManager networkManager = NetworkManager.getInstance(this);
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);

    }

    @Override
    protected void onResume() {
        super.onResume();
        authenticateUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void authenticateUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            List<AuthUI.IdpConfig> providers = Collections.singletonList(
                    new AuthUI.IdpConfig.EmailBuilder().build());
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setTheme(R.style.Theme_Foodassistant_NoActionBar)
                            .build(),
                    RC_SIGN_IN);
        }
        else {
            UserDataUtility.getUserData(user, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserDataUtility.getUserData(user, this);
                Log.i("USER LOGIN", user.getDisplayName());
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.onNavDestinationSelected(item, navController)
                || super.onOptionsItemSelected(item);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void openScanProduct(View view) {
        Intent intent = new Intent(MainActivity.this, ScanProductActivity.class);
        startActivity(intent);
    }

    public void openFavoriteProductsActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ConsumedProductsActivity.class);
        intent.putExtra("mode", "favorites");
        startActivity(intent);
    }

    public void openProductHistoryActivity(View view) {
        Intent intent = new Intent(MainActivity.this, ConsumedProductsActivity.class);
        intent.putExtra("mode", "history");
        startActivity(intent);
    }

    public void openLogGenericFoodActivity(View view) {
        Intent intent = new Intent(MainActivity.this, LogGenericFoodActivity.class);
        startActivity(intent);
    }

    public void openLogCustomMealActivity(View view) {
        Intent intent = new Intent(MainActivity.this, LogCustomMealActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFetchSuccess(AppUser appUser) {
        AppDataManager.initialize(appUser);
        appDataManager = AppDataManager.getInstance();
        userSharedViewModel.select(appUser);
    }

    @Override
    public void onFetchNotFound() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        AppUser appUser = new AppUser(firebaseUser.getDisplayName(), firebaseUser.getEmail());
        userSharedViewModel.select(appUser);
    }

    @Override
    public void onFetchFailure(String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Oops! Something went wrong. Please check your Internet connection and retry registering/logging in. Error message: " + errorMessage);
        builder.setPositiveButton("Retry", (dialog, which) -> authenticateUser());
    }
}