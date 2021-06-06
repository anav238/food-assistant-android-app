package com.example.food_assistant.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food_assistant.Adapters.CustomMealIngredientAdapter;
import com.example.food_assistant.Fragments.NutrientIntakeFragment;
import com.example.food_assistant.Fragments.ProductConsumptionEffectsFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.Models.AppDataManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Ingredient;
import com.example.food_assistant.Models.IngredientSummary;
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.MealSummary;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Models.ProductIdentifier;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ActivityResultContracts.GetBrandedProduct;
import com.example.food_assistant.Utils.ActivityResultContracts.GetGenericProduct;
import com.example.food_assistant.Utils.EventListeners.MealSummaryFetchListener;
import com.example.food_assistant.Utils.EventListeners.ProductDataFetchListener;
import com.example.food_assistant.Utils.Firebase.MealDataUtility;
import com.example.food_assistant.Utils.Firebase.ProductDataUtility;
import com.example.food_assistant.Utils.Mappers.MealMapper;
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

public class LogCustomMealActivity extends AppCompatActivity implements CustomMealIngredientAdapter.MealIngredientListener, MealSummaryFetchListener, ProductDataFetchListener {

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
    private String mealId = "";
    private AppDataManager appDataManager;
    private AppUser appUser;

    private String selectedMode = "log";
    private boolean requestedLogMeal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_custom_meal);

        if (savedInstanceState != null) {
            selectedMode = savedInstanceState.getString("mode");
            if (selectedMode != null && selectedMode.equals("edit"))
                mealId = savedInstanceState.getString("mealId");
        }
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("mode")) {
                selectedMode = bundle.getString("mode");
                if (selectedMode != null && selectedMode.equals("edit"))
                    mealId = bundle.getString("mealId");
            }
        }

        layoutFabScan = findViewById(R.id.linearLayout_scan_product);
        layoutFabLog = findViewById(R.id.linearLayout_add_generic_food);
        fabAdd = findViewById(R.id.fab_add);

        appDataManager = AppDataManager.getInstance();
        appUser = appDataManager.getAppUser();

        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);

        userSharedViewModel.select(appUser);

        setupActivityResultLaunchers();

        Log.i("INFO", "LogCustomMealActivitMode = " + selectedMode);
        if (selectedMode.equals("edit")) {
            showLoadingScreen();
            MealDataUtility.getMealSummaryById(mealId, this);
        }
        else
            setupMealIngredientsRecyclerView();
        setupFragmentResultListeners();
    }

    private void setupFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener("GET_QUANTITY_SUCCESS", this, (requestKey, bundle) -> {
            double productQuantity = bundle.getDouble("productQuantity");

            Ingredient oldIngredient = adapter.itemAt(currentItemAdapterPosition);
            meal.removeIngredient(oldIngredient);

            Ingredient updatedIngredient = new Ingredient(adapter.itemAt(currentItemAdapterPosition).getProduct(), productQuantity);
            meal.addIngredient(updatedIngredient);
            adapter.editIngredient(currentItemAdapterPosition, updatedIngredient);

            updateNutritionalValuesFragment();
        });

        getSupportFragmentManager().setFragmentResultListener("PROCESS_PRODUCT_SUCCESS", this, (requestKey, bundle) -> {
            MealDataUtility.logMealData(meal);

            appUser.updateUserNutrientConsumptionWithMeal(meal, meal.getTotalQuantity());
            appDataManager.setAppUser(appUser);
            userSharedViewModel.select(appUser);
            UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);

            CharSequence text = "Meal logged!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        });

    }

    private void setupMealIngredientsRecyclerView() {
        mealIngredientsRecyclerView = findViewById(R.id.recyclerView_meal_ingredients);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mealIngredientsRecyclerView.setLayoutManager(layoutManager);
        adapter = new CustomMealIngredientAdapter(meal.getIngredients(), this);
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

        meal.addIngredient(new Ingredient(product, 0.0));

        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), "Product added!", duration);
        toast.show();
        if (adapter.getItemCount() == 0) {
            TextView noIngredientsTextView = findViewById(R.id.textView_no_ingredients);
            noIngredientsTextView.setVisibility(View.GONE);
            mealIngredientsRecyclerView.setVisibility(View.VISIBLE);

            if (selectedMode.equals("log")) {
                Button logMealButton = findViewById(R.id.button_log_meal);
                logMealButton.setEnabled(true);

                Button saveMealButton = findViewById(R.id.button_save_meal);
                saveMealButton.setEnabled(true);
            }
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

            if (selectedMode.equals("log")) {
                Button logMealButton = findViewById(R.id.button_log_meal);
                logMealButton.setEnabled(false);

                Button saveMealButton = findViewById(R.id.button_save_meal);
                saveMealButton.setEnabled(false);
            }
        }
    }

    public void toggleShowNutritionalValues(View view) {
        CheckBox showNutritionalValuesCheckbox = findViewById(R.id.checkBox_show_nutritional_values);
        FragmentContainerView nutritionalValuesFragment = findViewById(R.id.fragment_nutritional_values);

        if (!showNutritionalValuesCheckbox.isChecked())
            nutritionalValuesFragment.setVisibility(View.GONE);
        else {
            if (!meal.isComplete())
                showLoadingScreen();
            nutritionalValuesFragment.setVisibility(View.VISIBLE);
            updateNutritionalValuesFragment();
        }
    }

    private void showLoadingScreen() {
        LinearLayout loadingLayout = findViewById(R.id.linearLayout_loading);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void hideLoadingScreen() {
        LinearLayout loadingLayout = findViewById(R.id.linearLayout_loading);
        loadingLayout.setVisibility(View.GONE);
    }

    private void updateNutritionalValuesFragment() {
        CheckBox showNutritionalValuesCheckbox = findViewById(R.id.checkBox_show_nutritional_values);
        if (!showNutritionalValuesCheckbox.isChecked())
            return;

        Map<String, Double> mealNutritionalValues = meal.getMealNutrition();
        Map<String, Double> nutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(mealNutritionalValues, appUser);

        System.out.println(meal.getIngredients());
        System.out.println(mealNutritionalValues);

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
        }
        else
            fragmentManager.beginTransaction().replace(R.id.fragment_nutritional_values, NutrientIntakeFragment.class, bundle, "todayNutrientIntake").commit();
    }

    public void logMeal(View view) {
        if (!isValidMeal())
            return;

        if (!meal.isComplete()) {
            showLoadingScreen();
            requestedLogMeal = true;
            return;
        }

        userSharedViewModel.select(appUser);

        EditText mealNameEditText = findViewById(R.id.editText_meal_name);
        String mealName = mealNameEditText.getText().toString();

        meal.setName(mealName);

        Map<String, Double> initialNutrientValues = appUser.getTodayNutrientConsumption();
        Map<String, Double> initialNutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(initialNutrientValues, appUser);

        Map<String, Double> totalNutrientValues = NutrientCalculator.addMealNutritionToUserDailyNutrition(initialNutrientValues, meal, meal.getTotalQuantity());
        Map<String, Double> totalNutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(totalNutrientValues, appUser);

        Bundle newFragmentBundle = new Bundle();
        newFragmentBundle.putDouble("productQuantity", meal.getTotalQuantity());
        newFragmentBundle.putStringArray("nutrients", totalNutrientPercentages.keySet().toArray(new String[totalNutrientPercentages.keySet().size()]));

        for (String nutrient:initialNutrientPercentages.keySet())
            newFragmentBundle.putDouble(nutrient + "_initial", initialNutrientPercentages.get(nutrient));

        for (String nutrient:totalNutrientPercentages.keySet())
            newFragmentBundle.putDouble(nutrient + "_final", totalNutrientPercentages.get(nutrient));

        ProductConsumptionEffectsFragment productConsumptionEffectsFragment = new ProductConsumptionEffectsFragment();
        productConsumptionEffectsFragment.setArguments(newFragmentBundle);
        productConsumptionEffectsFragment.show(getSupportFragmentManager(), "test");
    }

    public void saveMeal(View view) {
        if (!isValidMeal())
            return;

        EditText mealNameEditText = findViewById(R.id.editText_meal_name);
        String mealName = mealNameEditText.getText().toString();
        meal.setName(mealName);

        appUser.saveMealToHistory(meal);
        MealDataUtility.logMealData(meal);

        appDataManager.setAppUser(appUser);
        userSharedViewModel.select(appUser);
        UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);

        CharSequence text = "Meal saved!";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public boolean isValidMeal() {
        EditText mealNameEditText = findViewById(R.id.editText_meal_name);
        String mealName = mealNameEditText.getText().toString();
        if (mealName.length() == 0) {
            mealNameEditText.setError("Please enter a name for the meal!");
            return false;
        }
        return true;
    }

    @Override
    public void onFetchSuccess(MealSummary summary) {
        meal = MealMapper.mapMinimalMealDataFromSummary(summary);

        EditText mealNameEditText = findViewById(R.id.editText_meal_name);
        mealNameEditText.setText(meal.getName());

        for (IngredientSummary ingredientSummary:summary.getIngredients())
            ProductDataUtility.getProductByIdentifier(ingredientSummary.getProductIdentifier(), this);
    }

    @Override
    public void onFetchSuccess(Product product) {
        meal.completeIngredient(product);
        Log.i("INFO", "Fetched all data for product: " + product.toString());
        if (meal.isComplete()) {
            Log.i("INFO", "Meal complete: " + meal.toString());
            hideLoadingScreen();

            TextView noIngredientsTextView = findViewById(R.id.textView_no_ingredients);
            noIngredientsTextView.setVisibility(View.GONE);
            setupMealIngredientsRecyclerView();

            Button logMealButton = findViewById(R.id.button_log_meal);
            logMealButton.setEnabled(true);

            Button saveMealButton = findViewById(R.id.button_save_meal);
            saveMealButton.setEnabled(true);
            //updateNutritionalValuesFragment();
            //if (requestedLogMeal)
            //    logMeal(null);
            //requestedLogMeal = false;
        }
    }

    @Override
    public void onFetchNotFound() {
        new AlertDialog.Builder(this)
                .setTitle("Error fetching meal data")
                .setMessage("This meal does not exist anymore.")
                .setPositiveButton("Close", null)
                .show();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

    @Override
    public void onFetchFailure(String errorMessage) {
        new AlertDialog.Builder(this)
                .setTitle("Error fetching meal data")
                .setMessage("Error cause: " + errorMessage + ". If this is a connection error, retry after reconnecting to the Internet.")
                .setPositiveButton("Ok", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}