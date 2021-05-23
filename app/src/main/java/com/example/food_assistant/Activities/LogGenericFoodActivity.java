package com.example.food_assistant.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;

import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ViewModels.ProductListSharedViewModel;

public class LogGenericFoodActivity extends AppCompatActivity {

    private NetworkManager networkManager;
    private ProductListSharedViewModel productListSharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_generic_food);

        networkManager = NetworkManager.getInstance(this);

        productListSharedViewModel = new ViewModelProvider(this).get(ProductListSharedViewModel.class);
        productListSharedViewModel.getSelected().observe(this, products -> {
            Log.i("Product list changed", products.toString());
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
        //networkManager.searchFoodByName("strawberry", this);
    }

    private void updateFoodListWithQuery(String query) {
        networkManager.searchFoodByName(query, this);
    }
}