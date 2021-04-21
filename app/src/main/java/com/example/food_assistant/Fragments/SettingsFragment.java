package com.example.food_assistant.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.food_assistant.R;

import java.util.Objects;

public class SettingsFragment extends PreferenceFragmentCompat {

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        View carbsView = requireActivity().findViewById(R.id.carbs);
        if (carbsView != null)
            showNutrientMaxQuantity(carbsView);

        //SwitchPreference carbsPreference = findPreference("carbs_shown");
        /*carbsPreference.setOnPreferenceClickListener(preference -> {
            View carbsViewListener = requireActivity().findViewById(R.id.carbs);
            showNutrientMaxQuantity(carbsViewListener);
            return true;
        });*/
    }

    public void showNutrientMaxQuantity(View v) {

        String nutrient = "";

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(requireActivity());

        String shownValueKey = nutrient + "_shown";
        String maxValueKey = nutrient + "_max1";

        boolean nutrientShown = sharedPreferences.getBoolean(shownValueKey, true);

        if(nutrientShown) {
            EditTextPreference carbsPreference = findPreference(maxValueKey);
            if (carbsPreference != null) {
                carbsPreference.setVisible(true);
            }
        }
    }

}