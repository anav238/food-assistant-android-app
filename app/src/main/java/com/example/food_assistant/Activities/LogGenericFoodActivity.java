package com.example.food_assistant.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.example.food_assistant.Adapters.GenericFoodLookupAdapter;
import com.example.food_assistant.HttpRequest.NetworkManager;
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
        if (user != null)
            UserDataUtility.getUserData(user, userSharedViewModel);

        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);
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
    }

    private void updateFoodListWithQuery(String query) {
        networkManager.searchFoodByName(query, this);
    }
}