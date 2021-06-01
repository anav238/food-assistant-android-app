package com.example.food_assistant.Fragments;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Nutrition.Nutrients;

public class NutrientIntakeFragment extends Fragment {

    public NutrientIntakeFragment() { }

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
            if (nutrientPercentage >= 1)
                nutrientProgressBar.setProgress(nutrientPercentage);
            else
                nutrientProgressBar.setProgress(1);

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

            String textViewIdString = "textView_" + nutrient.replace("-", "_") + "_percentage";
            int textViewId = res.getIdentifier(textViewIdString, "id", this.getActivity().getPackageName());
            TextView nutrientTextView = view.findViewById(textViewId);
            nutrientTextView.setText(nutrientPercentage + "%");
        }
        return view;
    }
}