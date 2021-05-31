package com.example.food_assistant.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food_assistant.Adapters.CustomMealIngredientAdapter;
import com.example.food_assistant.Fragments.NutrientIntakeFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Ingredient;
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ActivityResultContracts.GetBrandedProduct;
import com.example.food_assistant.Utils.ActivityResultContracts.GetGenericProduct;
import com.example.food_assistant.Utils.Nutrition.NutrientCalculator;
import com.example.food_assistant.Utils.Nutrition.Nutrients;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LogCustomMealActivity extends AppCompatActivity implements CustomMealIngredientAdapter.MealIngredientListener {

    private boolean fabExpanded = false;
    private LinearLayout layoutFabScan;
    private LinearLayout layoutFabLog;
    private FloatingActionButton fabAdd;
    private ActivityResultLauncher<String> openScanProduct;
    private ActivityResultLauncher<String> openLogGenericFood;

    private RecyclerView mealIngredientsRecyclerView;
    private CustomMealIngredientAdapter adapter;

    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;
    private int currentItemAdapterPosition = -1;

    private Meal meal = new Meal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_custom_meal);
        layoutFabScan = findViewById(R.id.linearLayout_scan_product);
        layoutFabLog = findViewById(R.id.linearLayout_add_generic_food);
        fabAdd = findViewById(R.id.fab_add);

        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserDataUtility.getUserData(user, userSharedViewModel);
            userSharedViewModel.getSelected().observe(this, appUser -> {
                UserDataUtility.updateUserDataToDb(user, userSharedViewModel);
            });
        }

        setupActivityResultLaunchers();
        setupMealIngredientsRecyclerView();
        setupFragmentResultListeners();
    }

    private void setupFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener("GET_QUANTITY_SUCCESS", this, (requestKey, bundle) -> {
            double productQuantity = bundle.getDouble("productQuantity");

            Ingredient oldIngredient = adapter.itemAt(currentItemAdapterPosition);
            meal.removeIngredient(oldIngredient);

            Ingredient updatedIngredient = new Ingredient(adapter.itemAt(currentItemAdapterPosition).getProduct(), productQuantity);
            adapter.editIngredient(currentItemAdapterPosition, updatedIngredient);

            updateNutritionalValuesFragment();
        });
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

        product.setBaseQuantity(0.0);
        meal.addIngredient(new Ingredient(product, 0.0));

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), "Product added!", duration);
        toast.show();
        if (adapter.getItemCount() == 0) {
            TextView noIngredientsTextView = findViewById(R.id.textView_no_ingredients);
            noIngredientsTextView.setVisibility(View.GONE);
            mealIngredientsRecyclerView.setVisibility(View.VISIBLE);
        }
        adapter.addItem(new Ingredient(product, 0.0));
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

    @Override
    public void onPressEditButton(int ingredientAdapterPosition) {
        productSharedViewModel.select(adapter.itemAt(ingredientAdapterPosition).getProduct());
        currentItemAdapterPosition = ingredientAdapterPosition;
        SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        selectProductQuantityFragment.show(fragmentManager, "test");
    }

    @Override
    public void onPressRemoveButton(int ingredientAdapterPosition) {
        meal.removeIngredient(adapter.itemAt(ingredientAdapterPosition));

        adapter.removeItem(ingredientAdapterPosition);
        updateNutritionalValuesFragment();
        if (adapter.getItemCount() == 0) {
            TextView noIngredientsTextView = findViewById(R.id.textView_no_ingredients);
            noIngredientsTextView.setVisibility(View.VISIBLE);
            mealIngredientsRecyclerView.setVisibility(View.GONE);
        }
    }

    public void toggleShowNutritionalValues(View view) {
        CheckBox showNutritionalValuesCheckbox = findViewById(R.id.checkBox_show_nutritional_values);
        FragmentContainerView nutritionalValuesFragment = findViewById(R.id.fragment_nutritional_values);

        if (!showNutritionalValuesCheckbox.isChecked())
            nutritionalValuesFragment.setVisibility(View.GONE);
        else {
            nutritionalValuesFragment.setVisibility(View.VISIBLE);
            updateNutritionalValuesFragment();
        }
    }

    private void updateNutritionalValuesFragment() {
        AppUser currentUser = userSharedViewModel.getSelected().getValue();
        if (currentUser == null)
            return;

        Map<String, Double> mealNutritionalValues = meal.getMealNutrition();
        Map<String, Double> nutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(mealNutritionalValues, currentUser);
        Bundle bundle = new Bundle();
        bundle.putStringArray("nutrients", nutrientPercentages.keySet().toArray(new String[nutrientPercentages.keySet().size()]));
        for (String nutrient : nutrientPercentages.keySet())
            bundle.putInt(nutrient, (int) Math.round(nutrientPercentages.get(nutrient)));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("todayNutrientIntake");

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_nutritional_values, NutrientIntakeFragment.class, bundle, "todayNutrientIntake")
                    .commit();
        } else
            fragmentManager.beginTransaction().replace(R.id.fragment_nutritional_values, NutrientIntakeFragment.class, bundle, "todayNutrientIntake").commit();
    }



    public void logMeal(View view) {
        /*
        ProductConsumptionEffectsFragment productConsumptionEffectsFragment = new ProductConsumptionEffectsFragment();
        Bundle args = new Bundle();
        args.putDouble("productQuantity", productQuantity);
        productConsumptionEffectsFragment.setArguments(args);
        productConsumptionEffectsFragment.show(getSupportFragmentManager(), "test");*/
    }
}