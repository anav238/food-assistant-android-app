package com.example.food_assistant.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.food_assistant.Utils.ViewModels.ImageProcessorSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductListSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class LogGenericFoodActivity extends AppCompatActivity {

    private NetworkManager networkManager;

    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;
    private ProductListSharedViewModel productListSharedViewModel;

    private RecyclerView foodLookupRecyclerView;
    private GenericFoodLookupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_generic_food);

        networkManager = NetworkManager.getInstance(this);

        foodLookupRecyclerView = findViewById(R.id.recyclerView_food_lookup);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        foodLookupRecyclerView.setLayoutManager(layoutManager);
        adapter = new GenericFoodLookupAdapter(new ArrayList<>(), this);
        foodLookupRecyclerView.setAdapter(adapter);

        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserDataUtility.getUserData(user, userSharedViewModel);
            userSharedViewModel.getSelected().observe(this, appUser -> {
                UserDataUtility.updateUserDataToDb(user, userSharedViewModel);
            });
        }

        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);
        productSharedViewModel.getSelected().observe(this, products -> {
            SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            selectProductQuantityFragment.show(fragmentManager, "test");
        });

        productListSharedViewModel = new ViewModelProvider(this).get(ProductListSharedViewModel.class);
        productListSharedViewModel.getSelected().observe(this, products -> {
            Log.i("Product list changed", products.toString());
            adapter.setLocalDataSet(new ArrayList<>(products));
            foodLookupRecyclerView.setAdapter(adapter);
        });

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

        setupFragmentResultListeners();
    }

    private void setupFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener("GET_QUANTITY_SUCCESS", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                double productQuantity = bundle.getDouble("productQuantity");
                ProductConsumptionEffectsFragment productConsumptionEffectsFragment = new ProductConsumptionEffectsFragment();
                Bundle args = new Bundle();
                args.putDouble("productQuantity", productQuantity);
                productConsumptionEffectsFragment.setArguments(args);
                productConsumptionEffectsFragment.show(getSupportFragmentManager(), "test");
            }
        });

        getSupportFragmentManager().setFragmentResultListener("PROCESS_PRODUCT_SUCCESS", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                double productQuantity = bundle.getDouble("productQuantity");
                AppUser user = userSharedViewModel.getSelected().getValue();
                Product product = productSharedViewModel.getSelected().getValue();
                user.updateUserNutrientConsumption(product, productQuantity);
                userSharedViewModel.select(user);

                CharSequence text = "Product logged!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }

    private void updateFoodListWithQuery(String query) {
        networkManager.searchFoodByName(query, this);
    }
}