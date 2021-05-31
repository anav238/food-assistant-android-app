package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Nutrition.Nutrients;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class ProductConsumptionEffectsFragment extends DialogFragment {

    @NotNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        ProductSharedViewModel productSharedViewModel = new ViewModelProvider(requireActivity()).get(ProductSharedViewModel.class);
        Product product = productSharedViewModel.getSelected().getValue();

        double productQuantity = requireArguments().getDouble("productQuantity");

        String[] nutrients = requireArguments().getStringArray("nutrients");

        Map<String, Double> initialNutrientValues = new HashMap<>();
        Map<String, Double> finalNutrientValues = new HashMap<>();
        for (String nutrient:nutrients) {
            double nutrientInitialPercentage = requireArguments().getDouble(nutrient + "_initial");
            double nutrientFinalPercentage = requireArguments().getDouble(nutrient + "_final");
            initialNutrientValues.put(nutrient, nutrientInitialPercentage);
            finalNutrientValues.put(nutrient, nutrientFinalPercentage);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.fragment_product_consumption_effects, null);
        builder.setView(content)
                .setMessage("Are you sure you want to consume this product?")
                .setPositiveButton("Log Product", (dialog, id) -> {
                    Bundle result = new Bundle();
                    result.putDouble("productQuantity", productQuantity);
                    getParentFragmentManager().setFragmentResult("PROCESS_PRODUCT_SUCCESS", result);
                    dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    Bundle result = new Bundle();
                    getParentFragmentManager().setFragmentResult("PROCESS_PRODUCT_CANCEL", result);
                    dismiss();
                });

        populateProductNutrientData(content, product, initialNutrientValues, finalNutrientValues);

        return builder.create();
    }

    private void populateProductNutrientData(View content, Product product, Map<String, Double> initialNutrientValues, Map<String, Double> finalNutrientValues) {
        TextView nutriScoreGradeTextView = content.findViewById(R.id.nutriScoreGrade);
        nutriScoreGradeTextView.setText(product.getNutriScoreGrade());

        TextView novaScoreGradeTextView = content.findViewById(R.id.novaScoreGrade);
        novaScoreGradeTextView.setText(product.getNovaGroup());

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        for (String nutrient:initialNutrientValues.keySet()) {
            String nutrientIncreaseTextViewIdString = "textView_" + nutrient.replace("-", "_") + "_increase";
            Resources res = getResources();
            int nutrientIncreaseTextViewId = res.getIdentifier(nutrientIncreaseTextViewIdString, "id", this.getActivity().getPackageName());

            TextView nutrientIncreaseTextView = content.findViewById(nutrientIncreaseTextViewId);
            CharSequence nutrientIncreaseText = decimalFormat.format(initialNutrientValues.get(nutrient)) + "% ->" + decimalFormat.format(finalNutrientValues.get(nutrient)) + "%";
            nutrientIncreaseTextView.setText(nutrientIncreaseText);
        }

    }

    @Override
    public void onDismiss (@NotNull DialogInterface dialog) {

    }
}