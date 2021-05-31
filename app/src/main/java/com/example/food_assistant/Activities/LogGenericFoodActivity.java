package com.example.food_assistant.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.food_assistant.Adapters.GenericFoodLookupAdapter;
import com.example.food_assistant.Fragments.ProductConsumptionEffectsFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.Nutrition.NutrientCalculator;
import com.example.food_assistant.Utils.ViewModels.ProductListSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Map;

public class LogGenericFoodActivity extends AppCompatActivity {

    private NetworkManager networkManager;

    private static final String SINGLE_LOG_MODE = "Single Log Mode";
    private static final String MULTIPLE_LOG_MODE = "Multiple Log Mode";
    private static final String STATE_SELECTED_LOG_MODE = "selected_log_mode";
    private String selectedLogMode = MULTIPLE_LOG_MODE;

    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;

    private RecyclerView foodLookupRecyclerView;
    private GenericFoodLookupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_generic_food);
        if (savedInstanceState != null) {
            selectedLogMode = savedInstanceState.getString(STATE_SELECTED_LOG_MODE, MULTIPLE_LOG_MODE);
        }
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey(STATE_SELECTED_LOG_MODE))
                selectedLogMode = bundle.getString(STATE_SELECTED_LOG_MODE);
        }

        networkManager = NetworkManager.getInstance(this);

        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserDataUtility.getUserData(user, userSharedViewModel);
            userSharedViewModel.getSelected().observe(this, appUser -> {
                UserDataUtility.updateUserDataToDb(user, userSharedViewModel);
            });
        }

        setupRecyclerView();
        setupObservers();
        setupFragmentResultListeners();
        setupSearchViewListeners();
    }

    private void setupSearchViewListeners() {
        SearchView foodSearchView = findViewById(R.id.searchView_food);
        foodSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateFoodListWithQuery(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                updateFoodListWithQuery(newText);
                return false;
            }
        });
    }

    private void setupRecyclerView() {
        foodLookupRecyclerView = findViewById(R.id.recyclerView_food_lookup);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        foodLookupRecyclerView.setLayoutManager(layoutManager);
        adapter = new GenericFoodLookupAdapter(new ArrayList<>(), this);
        foodLookupRecyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);
        productSharedViewModel.getSelected().observe(this, product -> {
            if (selectedLogMode.equals(MULTIPLE_LOG_MODE)) {
                SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
                FragmentManager fragmentManager = this.getSupportFragmentManager();
                selectProductQuantityFragment.show(fragmentManager, "test");
            }
            else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("product", product);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        ProductListSharedViewModel productListSharedViewModel = new ViewModelProvider(this).get(ProductListSharedViewModel.class);
        productListSharedViewModel.getSelected().observe(this, products -> {
            Log.i("Product list changed", products.toString());
            adapter.setLocalDataSet(new ArrayList<>(products));
            foodLookupRecyclerView.setAdapter(adapter);
        });
    }

    private void setupFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener("GET_QUANTITY_SUCCESS", this, (requestKey, bundle) -> {
            double productQuantity = bundle.getDouble("productQuantity");
            AppUser currentUser = userSharedViewModel.getSelected().getValue();
            Product currentProduct = productSharedViewModel.getSelected().getValue();

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
            AppUser user = userSharedViewModel.getSelected().getValue();
            Product product = productSharedViewModel.getSelected().getValue();
            user.updateUserNutrientConsumptionWithProduct(product, productQuantity);
            userSharedViewModel.select(user);

            CharSequence text = "Product logged!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

    }

    private void updateFoodListWithQuery(String query) {
        networkManager.searchFoodByName(query, this);
    }
}