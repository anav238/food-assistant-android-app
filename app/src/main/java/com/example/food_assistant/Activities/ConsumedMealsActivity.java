package com.example.food_assistant.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food_assistant.Adapters.ConsumedMealsAdapter;
import com.example.food_assistant.Adapters.ConsumedProductsAdapter;
import com.example.food_assistant.Fragments.ProductConsumptionEffectsFragment;
import com.example.food_assistant.Fragments.ProductInfoFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Models.AppDataManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.MealIdentifier;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Models.ProductIdentifier;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.EventListeners.MealDataFetchListener;
import com.example.food_assistant.Utils.EventListeners.ProductDataFetchListener;
import com.example.food_assistant.Utils.Firebase.MealDataUtility;
import com.example.food_assistant.Utils.Firebase.ProductDataUtility;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.Nutrition.NutrientCalculator;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConsumedMealsActivity extends AppCompatActivity implements ConsumedMealsAdapter.ConsumedMealListener, MealDataFetchListener {

    private String mode;
    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;

    private ConsumedMealsAdapter adapter;

    private int currentAdapterPosition = 0;
    private AppDataManager appDataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumed_meals);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

        NetworkManager networkManager = NetworkManager.getInstance(this);

        appDataManager = AppDataManager.getInstance();
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AppUser appUser = appDataManager.getAppUser();
            userSharedViewModel.select(appUser);
            if (mode.equals("history")) {
                List<MealIdentifier> mealIdentifiers = appUser.getMealHistory();
                boolean[] areFavorites = MealDataUtility.determineIfProductsAreFavoriteForUser(mealIdentifiers, appUser);
                setupRecyclerView(mealIdentifiers, areFavorites);
            }
            else {
                List<MealIdentifier> mealIdentifiers = appUser.getMealFavorites();
                boolean[] areFavorites = new boolean[mealIdentifiers.size()];
                Arrays.fill(areFavorites, Boolean.TRUE);
                setupRecyclerView(mealIdentifiers, areFavorites);
            }
        }

        setupFragmentResultListeners();
    }

    private void setupRecyclerView(List<MealIdentifier> mealIdentifiers, boolean[] areFavorites) {
        if (mealIdentifiers.size() > 0) {
            RecyclerView consumedMealsRecyclerView = findViewById(R.id.recyclerView_consumed_meals);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            consumedMealsRecyclerView.setLayoutManager(layoutManager);

            adapter = new ConsumedMealsAdapter(mealIdentifiers, areFavorites, this);
            consumedMealsRecyclerView.setAdapter(adapter);
        }
        else {
            TextView noProductsTextView = findViewById(R.id.textView_no_products);
            noProductsTextView.setVisibility(View.VISIBLE);
        }
    }

    private void setupFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener("LOG_PRODUCT_REQUEST", this, (requestKey, bundle) -> {
            SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            selectProductQuantityFragment.show(fragmentManager, "test");
        });

        getSupportFragmentManager().setFragmentResultListener("GET_QUANTITY_SUCCESS", this, (requestKey, bundle) -> {
            double productQuantity = bundle.getDouble("productQuantity");
            Product currentProduct = productSharedViewModel.getSelected().getValue();
            AppUser currentUser = appDataManager.getAppUser();

            Map<String, Double> initialNutrientValues = currentUser.getTodayNutrientConsumption();
            Map<String, Double> initialNutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(initialNutrientValues, currentUser);
            Map<String, Double> productNutrientValues = currentProduct.getNutriments();

            Map<String, Double> totalNutrientValues = NutrientCalculator.addProductNutritionToUserDailyNutrition(initialNutrientValues, productNutrientValues, productQuantity);
            Map<String, Double> totalNutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(totalNutrientValues, currentUser);

            Bundle newFragmentBundle = new Bundle();
            newFragmentBundle.putDouble("productQuantity", productQuantity);
            newFragmentBundle.putStringArray("nutrients", totalNutrientPercentages.keySet().toArray(new String[totalNutrientPercentages.keySet().size()]));

            for (String nutrient:initialNutrientPercentages.keySet())
                newFragmentBundle.putDouble(nutrient + "_initial", initialNutrientPercentages.get(nutrient));

            for (String nutrient:totalNutrientPercentages.keySet())
                newFragmentBundle.putDouble(nutrient + "_final", totalNutrientPercentages.get(nutrient));

            ProductConsumptionEffectsFragment productConsumptionEffectsFragment = new ProductConsumptionEffectsFragment();
            productConsumptionEffectsFragment.setArguments(newFragmentBundle);
            productConsumptionEffectsFragment.show(getSupportFragmentManager(), "test");
        });

        getSupportFragmentManager().setFragmentResultListener("PROCESS_PRODUCT_SUCCESS", this, (requestKey, bundle) -> {
            double productQuantity = bundle.getDouble("productQuantity");
            AppUser user = appDataManager.getAppUser();
            Product product = productSharedViewModel.getSelected().getValue();
            user.updateUserNutrientConsumptionWithProduct(product, productQuantity);
            appDataManager.setAppUser(user);
            userSharedViewModel.select(user);

            UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);

            CharSequence text = "Product logged!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });
    }

    @Override
    public void onPressFavoriteButton(int adapterPosition) {
        AppUser currentUser = appDataManager.getAppUser();
        if (currentUser != null) {
            if (!adapter.getIsFavorite(adapterPosition)) {
                currentUser.addMealFavorite(adapter.itemAt(adapterPosition));
                adapter.setIsFavorite(adapterPosition, true);
            }
            else {
                currentUser.removeMealFromFavorites(adapter.itemAt(adapterPosition));
                adapter.setIsFavorite(adapterPosition, false);
            }
            appDataManager.setAppUser(currentUser);
            userSharedViewModel.select(currentUser);
            UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);
        }
        currentAdapterPosition = adapterPosition;
    }

    @Override
    public void onPressEditButton(int productAdapterPosition) {
        /*LinearLayout loadingLayout = findViewById(R.id.linearLayout_loading);
        loadingLayout.setVisibility(View.VISIBLE);
        ProductDataUtility.getProductByIdentifier(adapter.itemAt(productAdapterPosition), this);
        currentAdapterPosition = productAdapterPosition;*/
    }

    @Override
    public void onFetchSuccess(Meal meal) {
        /*LinearLayout loadingLayout = findViewById(R.id.linearLayout_loading);
        loadingLayout.setVisibility(View.GONE);

        productSharedViewModel.select(product);

        ProductInfoFragment productInfoFragment = new ProductInfoFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        productInfoFragment.show(fragmentManager, "test");*/
    }

    @Override
    public void onFetchNotFound() {
        /*new AlertDialog.Builder(this)
                .setTitle("Error fetching product data")
                .setMessage("This product does not exist anymore.")
                .setPositiveButton("Close", null)
                .show();
        MealIdentifier mealIdentifier = adapter.itemAt(currentAdapterPosition);
        AppUser currentUser = appDataManager.getAppUser();
        if (currentUser != null) {
            currentUser.removeProductFromFavorites(productIdentifier);
            currentUser.removeProductFromHistory(productIdentifier);
            appDataManager.setAppUser(currentUser);
            userSharedViewModel.select(currentUser);
            UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);
        }*/
    }

    @Override
    public void onFetchFailure(String errorMessage) {
       /* new AlertDialog.Builder(this)
                .setTitle("Error fetching product data")
                .setMessage("Error cause: " + errorMessage + ". If this is a connection error, retry after reconnecting to the Internet.")
                .setPositiveButton("Ok", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/
    }

}