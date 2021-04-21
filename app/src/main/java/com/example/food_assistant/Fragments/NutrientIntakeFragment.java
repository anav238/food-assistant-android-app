package com.example.food_assistant.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.food_assistant.R;

import java.util.ArrayList;
import java.util.List;

public class NutrientIntakeFragment extends Fragment {

    private static List<String> nutrients;
    private static List<String> values;

    public NutrientIntakeFragment() {
        // Required empty public constructor
    }

    public static NutrientIntakeFragment newInstance(ArrayList<String> nutrients, ArrayList<Integer> values) {
        NutrientIntakeFragment fragment = new NutrientIntakeFragment();
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