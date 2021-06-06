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
import com.example.food_assistant.Models.Meal;
import com.example.food_assistant.Models.MealSummary;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ActivityResultContracts.GetBrandedProduct;
import com.example.food_assistant.Utils.ActivityResultContracts.GetGenericProduct;
import com.example.food_assistant.Utils.EventListeners.MealSummaryFetchListener;
import com.example.food_assistant.Utils.Firebase.MealDataUtility;
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

public class LogCustomMealActivity extends AppCompatActivity implements CustomMealIngredientAdapter.MealIngredientListener, MealSummaryFetchListener {

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
    private MealSummary mealSummary = new MealSummary();
    private String mealId = "";
    private AppDataManager appDataManager;

    private String selectedMode = "log";

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
        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);

        userSharedViewModel.select(appDataManager.getAppUser());

        setupActivityResultLaunchers();

        if (selectedMode.equals("edit")) {
            LinearLayout loadingLayout = findViewById(R.id.linearLayout_loading);
            loadingLayout.setVisibility(View.VISIBLE);
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

            AppUser user = appDataManager.getAppUser();
            user.updateUserNutrientConsumptionWithMeal(meal, meal.getTotalQuantity());
            appDataManager.setAppUser(user);
            userSharedViewModel.select(user);
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

            Button logMealButton = findViewById(R.id.button_log_meal);
            logMealButton.setEnabled(true);
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

            Button logMealButton = findViewById(R.id.button_log_meal);
            logMealButton.setEnabled(false);
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
        AppUser currentUser = appDataManager.getAppUser();
        if (currentUser == null)
            return;

        CheckBox showNutritionalValuesCheckbox = findViewById(R.id.checkBox_show_nutritional_values);
        if (!showNutritionalValuesCheckbox.isChecked())
            return;

        Map<String, Double> mealNutritionalValues = meal.getMealNutrition();
        Map<String, Double> nutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(mealNutritionalValues, currentUser);

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
        EditText mealNameEditText = findViewById(R.id.editText_meal_name);
        String mealName = mealNameEditText.getText().toString();
        if (mealName.length() == 0) {
            mealNameEditText.setError("Please enter a name for the meal!");
            return;
        }

        meal.setName(mealName);

        AppUser currentUser = appDataManager.getAppUser();

        Map<String, Double> initialNutrientValues = currentUser.getTodayNutrientConsumption();
        Map<String, Double> initialNutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(initialNutrientValues, currentUser);

        Map<String, Double> totalNutrientValues = NutrientCalculator.addMealNutritionToUserDailyNutrition(initialNutrientValues, meal, meal.getTotalQuantity());
        Map<String, Double> totalNutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(totalNutrientValues, currentUser);

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

    @Override
    public void onFetchSuccess(MealSummary summary) {
        LinearLayout loadingLayout = findViewById(R.id.linearLayout_loading);
        loadingLayout.setVisibility(View.VISIBLE);
        meal = MealMapper.mapMinimalMealDataFromSummary(mealSummary);
        setupMealIngredientsRecyclerView();
    }

    @Override
    public void onFetchNotFound() {

    }

    @Override
    public void onFetchFailure(String errorMessage) {

    }
}