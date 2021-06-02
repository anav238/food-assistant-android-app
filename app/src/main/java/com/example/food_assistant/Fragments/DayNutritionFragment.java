package com.example.food_assistant.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Nutrition.NutrientCalculator;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import java.util.Map;

public class DayNutritionFragment extends Fragment {

    private UserSharedViewModel userSharedViewModel;

    public DayNutritionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        userSharedViewModel.getSelected().observe(this,  provider -> showNutrientIntakeFragment());
    }

    @Override
    public void onResume() {
        super.onResume();
        showNutrientIntakeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_day_nutrition, container, false);
    }

    private void showNutrientIntakeFragment() {
        AppUser currentUser = userSharedViewModel.getSelected().getValue();
        if (currentUser == null)
            return;

        Map<String, Double> todayNutrientConsumption = currentUser.getTodayNutrientConsumption();
        Map<String, Double> nutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(todayNutrientConsumption, currentUser);

        Bundle bundle = new Bundle();
        bundle.putStringArray("nutrients", nutrientPercentages.keySet().toArray(new String[nutrientPercentages.keySet().size()]));
        for (String nutrient:nutrientPercentages.keySet())
            bundle.putInt(nutrient, (int) Math.round(nutrientPercentages.get(nutrient)));


        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("todayNutrientIntake");

        ProgressBar progressBar = getView().findViewById(R.id.progress_bar_nutrient_intake);
        progressBar.setVisibility(View.GONE);

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.nutrientIntakeContainer, NutrientIntakeFragment.class, bundle, "todayNutrientIntake")
                    .commit();
        }
        else
            fragmentManager.beginTransaction().replace(R.id.nutrientIntakeContainer, NutrientIntakeFragment.class, bundle, "todayNutrientIntake").commit();

    }
}