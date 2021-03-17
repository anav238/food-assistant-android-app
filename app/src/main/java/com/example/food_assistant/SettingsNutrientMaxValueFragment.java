package com.example.food_assistant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsNutrientMaxValueFragment extends PreferenceFragmentCompat {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public SettingsNutrientMaxValueFragment() {
        // Required empty public constructor
    }

    public static SettingsNutrientMaxValueFragment newInstance(String param1, String param2) {
        SettingsNutrientMaxValueFragment fragment = new SettingsNutrientMaxValueFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

}