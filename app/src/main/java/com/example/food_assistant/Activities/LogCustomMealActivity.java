package com.example.food_assistant.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food_assistant.Adapters.CustomMealIngredientAdapter;
import com.example.food_assistant.Adapters.GenericFoodLookupAdapter;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ActivityResultContracts.GetBrandedProduct;
import com.example.food_assistant.Utils.ActivityResultContracts.GetGenericProduct;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class LogCustomMealActivity extends AppCompatActivity {

    private boolean fabExpanded = false;
    private LinearLayout layoutFabScan;
    private LinearLayout layoutFabLog;
    private FloatingActionButton fabAdd;
    private ActivityResultLauncher<String> openScanProduct;
    private ActivityResultLauncher<String> openLogGenericFood;

    private RecyclerView mealIngredientsRecyclerView;
    private CustomMealIngredientAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_custom_meal);
        layoutFabScan = findViewById(R.id.linearLayout_scan_product);
        layoutFabLog = findViewById(R.id.linearLayout_add_generic_food);
        fabAdd = findViewById(R.id.fab_add);

        setupActivityResultLaunchers();
        setupMealIngredientsRecyclerView();
    }

    private void setupMealIngredientsRecyclerView() {
        mealIngredientsRecyclerView = findViewById(R.id.recyclerView_meal_ingredients);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mealIngredientsRecyclerView.setLayoutManager(layoutManager);
        adapter = new CustomMealIngredientAdapter(new ArrayList<>(), this);
        mealIngredientsRecyclerView.setAdapter(adapter);
    }

    private void setupActivityResultLaunchers() {
        openScanProduct = registerForActivityResult(new GetBrandedProduct(),
                this::processAddedProduct);

        openLogGenericFood = registerForActivityResult(new GetGenericProduct(),
                this::processAddedProduct);
    }

    private void processAddedProduct(Product product) {
        if (product == null)
            return;

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), "Product added!", duration);
        toast.show();
        if (adapter.getItemCount() == 0) {
            TextView noIngredientsTextView = findViewById(R.id.textView_no_ingredients);
            noIngredientsTextView.setVisibility(View.GONE);
        }
        adapter.addItem(product);
    }

    public void toggleSubMenusFab(View view) {
        if (!fabExpanded)
            openSubMenusFab();
        else
            closeSubMenusFab();
    }

    private void closeSubMenusFab(){
        layoutFabScan.setVisibility(View.INVISIBLE);
        layoutFabLog.setVisibility(View.INVISIBLE);
        fabAdd.setImageResource(R.drawable.ic_plus);
        fabExpanded = false;
    }

    private void openSubMenusFab(){
        layoutFabScan.setVisibility(View.VISIBLE);
        layoutFabLog.setVisibility(View.VISIBLE);
        fabAdd.setImageResource(R.drawable.ic_close);
        fabExpanded = true;
    }

    public void openScanProductActivity(View view) {
        openScanProduct.launch("Single Scan Mode");
        closeSubMenusFab();
    }

    public void openLogGenericFoodActivity(View view) {
        openLogGenericFood.launch("Single Log Mode");
        closeSubMenusFab();
    }

}