package com.example.food_assistant.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Constants.Nutrients;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import java.util.Map;

public class DayNutritionFragment extends Fragment {

    private UserSharedViewModel userSharedViewModel;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public DayNutritionFragment() {
        // Required empty public constructor
    }

    public static DayNutritionFragment newInstance(String param1, String param2) {
        DayNutritionFragment fragment = new DayNutritionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day_nutrition, container, false);
    }

    private void showNutrientIntakeFragment() {
        AppUser currentUser = userSharedViewModel.getSelected().getValue();
        if (currentUser == null)
            return;

        Map<String, Double> maxNutrientDVs = currentUser.getMaximumNutrientDV();
        Map<String, Double> todayNutrientConsumption = currentUser.getTodayNutrientConsumption();
        Bundle bundle = new Bundle();
        bundle.putStringArray("nutrients", maxNutrientDVs.keySet().toArray(new String[maxNutrientDVs.keySet().size()]));
        for (String nutrient:maxNutrientDVs.keySet()) {
            int nutrientPercentage = (int) (todayNutrientConsumption.get(nutrient) * 100 / maxNutrientDVs.get(nutrient));
            bundle.putInt(nutrient, nutrientPercentage);
        }

        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        //FragmentTransaction fTransaction = fManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag("todayNutrientIntake");

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.nutrientIntakeContainer, NutrientIntakeFragment.class, bundle, "todayNutrientIntake")
                    .commit();
        }
        else { // re-use the old fragment
            fragmentManager.beginTransaction().replace(R.id.nutrientIntakeContainer, NutrientIntakeFragment.class, bundle, "todayNutrientIntake").commit();
        }

    }
}