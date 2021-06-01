package com.example.food_assistant.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_assistant.Adapters.ConsumedProductsAdapter;
import com.example.food_assistant.Adapters.CustomMealIngredientAdapter;
import com.example.food_assistant.Adapters.GenericFoodLookupAdapter;
import com.example.food_assistant.Fragments.ProductConsumptionEffectsFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Models.ProductIdentifier;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Firebase.ProductDataUtility;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.Nutrition.NutrientCalculator;
import com.example.food_assistant.Utils.ViewModels.ProductListSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductHistoryActivity extends AppCompatActivity implements ConsumedProductsAdapter.ConsumedProductListener {

    private NetworkManager networkManager;
    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;

    private RecyclerView consumedProductsRecyclerView;
    private ConsumedProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_history);

        networkManager = NetworkManager.getInstance(this);

        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserDataUtility.getUserData(user, userSharedViewModel);
            userSharedViewModel.getSelected().observe(this, appUser -> {
                UserDataUtility.updateUserDataToDb(user, userSharedViewModel);
            });
        }

        AppUser appUser = userSharedViewModel.getSelected().getValue();
        List<ProductIdentifier> productIdentifiers = appUser.getProductHistory();
        boolean[] areFavorites = ProductDataUtility.determineIfProductsAreFavoriteForUser(productIdentifiers, appUser);

        setupRecyclerView(productIdentifiers, areFavorites);
        //setupObservers();
        //setupFragmentResultListeners();
    }

    private void setupRecyclerView(List<ProductIdentifier> productIdentifiers, boolean[] areFavorites) {
        consumedProductsRecyclerView = findViewById(R.id.recyclerView_consumed_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        consumedProductsRecyclerView.setLayoutManager(layoutManager);

        adapter = new ConsumedProductsAdapter(productIdentifiers, areFavorites, this);
        consumedProductsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onPressFavoriteButton(int productAdapterPosition) {

    }

    @Override
    public void onPressInfoButton(int productAdapterPosition) {

    }

    /*
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

    }*/
}