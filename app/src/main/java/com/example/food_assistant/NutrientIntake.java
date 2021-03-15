package com.example.food_assistant;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NutrientIntake extends Fragment {

    private static List<String> nutrients;
    private static List<String> values;

    public NutrientIntake() {
        // Required empty public constructor
    }

    public static NutrientIntake newInstance(ArrayList<String> nutrients, ArrayList<Integer> values) {
        NutrientIntake fragment = new NutrientIntake();
        Bundle args = new Bundle();
        args.putStringArrayList("nutrients", nutrients);
        args.putIntegerArrayList("values", values);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nutrient_intake, container, false);
    }
}