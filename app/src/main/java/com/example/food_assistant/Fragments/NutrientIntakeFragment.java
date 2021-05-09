package com.example.food_assistant.Fragments;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Constants.Nutrients;

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
        String[] nutrients = requireArguments().getStringArray("nutrients");

        View view = inflater.inflate(R.layout.fragment_nutrient_intake, container, false);
        for (String nutrient:nutrients) {
            int nutrientPercentage = requireArguments().getInt(nutrient);
            String progressBarIdString = "progressBar_" + nutrient.replace("-", "_");
            Resources res = getResources();
            int progressBarId = res.getIdentifier(progressBarIdString, "id", this.getActivity().getPackageName());

            ProgressBar nutrientProgressBar = view.findViewById(progressBarId);
            nutrientProgressBar.setProgress(nutrientPercentage);

            int progressDrawableId = R.drawable.custom_progressbar_red;
            if (Nutrients.badNutrients.contains(nutrient)) {
                if (nutrientPercentage < 30)
                    progressDrawableId = R.drawable.custom_progressbar_green;
                else if (nutrientPercentage < 60)
                    progressDrawableId = R.drawable.custom_progressbar_yellow;
            }
            else {
                if (nutrientPercentage > 60)
                    progressDrawableId = R.drawable.custom_progressbar_green;
                else if (nutrientPercentage > 30)
                    progressDrawableId = R.drawable.custom_progressbar_yellow;
            }

            Drawable progressDrawable = getResources().getDrawable(progressDrawableId);
            nutrientProgressBar.setProgressDrawable(progressDrawable);
        }
        return view;
    }
}