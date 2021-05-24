package com.example.food_assistant.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.food_assistant.Adapters.GenericFoodLookupAdapter;
import com.example.food_assistant.Adapters.RecipeIngredientAdapter;
import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.ViewModels.ProductListSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.RecipeSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RecipeAnalysisActivity extends AppCompatActivity {

    private RecyclerView recipeIngredientsRecyclerView;
    private RecipeIngredientAdapter adapter;
    private RecipeSharedViewModel recipeSharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_analysis);

        recipeIngredientsRecyclerView = findViewById(R.id.recyclerView_recipe_ingredients);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recipeIngredientsRecyclerView.setLayoutManager(layoutManager);
        adapter = new RecipeIngredientAdapter(new ArrayList<>(), this);
        recipeIngredientsRecyclerView.setAdapter(adapter);

        recipeSharedViewModel = new ViewModelProvider(this).get(RecipeSharedViewModel.class);
    }

    public void parseRecipeIngredients(View view) {
        TextView recipeUrlTextView = findViewById(R.id.editText_recipe_url);
        String recipeUrl = recipeUrlTextView.getText().toString();
        NetworkManager.getInstance().getRecipeFromUrl(recipeUrl, this);
    }
}