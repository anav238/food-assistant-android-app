package com.example.food_assistant.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_assistant.Adapters.ConsumedProductsAdapter;
import com.example.food_assistant.Fragments.ProductInfoFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Models.ProductIdentifier;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Firebase.ProductDataUtility;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConsumedProductsActivity extends AppCompatActivity implements ConsumedProductsAdapter.ConsumedProductListener {

    private String mode;

    private NetworkManager networkManager;
    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;

    private RecyclerView consumedProductsRecyclerView;
    private ConsumedProductsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_history);

        Intent intent = getIntent();
        mode = intent.getStringExtra("mode");

        networkManager = NetworkManager.getInstance(this);

        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserDataUtility.getUserData(user, userSharedViewModel);
            userSharedViewModel.getSelected().observe(this, appUser -> {
                if (mode.equals("history")) {
                    List<ProductIdentifier> productIdentifiers = appUser.getProductHistory();
                    boolean[] areFavorites = ProductDataUtility.determineIfProductsAreFavoriteForUser(productIdentifiers, appUser);
                    setupRecyclerView(productIdentifiers, areFavorites);
                }
                else {
                    List<ProductIdentifier> productIdentifiers = appUser.getProductFavorites();
                    boolean[] areFavorites = new boolean[productIdentifiers.size()];
                    Arrays.fill(areFavorites, Boolean.TRUE);
                    setupRecyclerView(productIdentifiers, areFavorites);
                }

            });
        }

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
        AppUser currentUser = userSharedViewModel.getSelected().getValue();
        if (currentUser != null) {
            if (!adapter.getIsFavorite(productAdapterPosition)) {
                currentUser.addProductFavorite(adapter.itemAt(productAdapterPosition));
                adapter.setIsFavorite(productAdapterPosition, true);
            }
            else {
                currentUser.removeProductFavorite(adapter.itemAt(productAdapterPosition));
                adapter.setIsFavorite(productAdapterPosition, false);
            }
            UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);
        }
    }

    @Override
    public void onPressInfoButton(int productAdapterPosition) {
        productSharedViewModel.getSelected().observe(this, product -> {
            ProductInfoFragment productInfoFragment = new ProductInfoFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            productInfoFragment.show(fragmentManager, "test");
        });
        System.out.println(adapter.itemAt(productAdapterPosition));
        ProductDataUtility.getProductByIdentifier(adapter.itemAt(productAdapterPosition), productSharedViewModel);
    }

}