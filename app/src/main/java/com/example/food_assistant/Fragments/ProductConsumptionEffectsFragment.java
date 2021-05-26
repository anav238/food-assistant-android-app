package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.OpenFoodFactsProduct;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.MLKit.VisionImageProcessor;
import com.example.food_assistant.Utils.Constants.Nutrients;
import com.example.food_assistant.Utils.ViewModels.ImageProcessorSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

public class ProductConsumptionEffectsFragment extends DialogFragment {

    @NotNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        UserSharedViewModel userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        ProductSharedViewModel productSharedViewModel = new ViewModelProvider(requireActivity()).get(ProductSharedViewModel.class);
        ImageProcessorSharedViewModel imageProcessorSharedViewModel = new ViewModelProvider(requireActivity()).get(ImageProcessorSharedViewModel.class);

        AppUser user = userSharedViewModel.getSelected().getValue();
        Product product = productSharedViewModel.getSelected().getValue();
        VisionImageProcessor visionImageProcessor = imageProcessorSharedViewModel.getSelected().getValue();

        Double productQuantity = requireArguments().getDouble("productQuantity");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.fragment_product_consumption_effects, null);
        builder.setView(content)
                .setMessage("Are you sure you want to consume this product?")
                .setPositiveButton("Log Product", (dialog, id) -> {
                    //updateUserNutrientConsumption(productQuantity);
                    Bundle result = new Bundle();
                    result.putDouble("productQuantity", productQuantity);
                    getParentFragmentManager().setFragmentResult("PROCESS_PRODUCT_SUCCESS", result);
                    dismiss();
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // AppUser cancelled the dialog
                    if (visionImageProcessor != null) {
                        visionImageProcessor.restart();
                    }
                });

        populateProductNutrientData(content, user, product, productQuantity);

        return builder.create();
    }

    private void populateProductNutrientData(View content, AppUser user, Product product, double productQuantity) {
        TextView nutriScoreGradeTextView = content.findViewById(R.id.nutriScoreGrade);
        nutriScoreGradeTextView.setText(product.getNutriScoreGrade());

        TextView novaScoreGradeTextView = content.findViewById(R.id.novaScoreGrade);
        novaScoreGradeTextView.setText(product.getNovaGroup());

        Map<String, Double> maxNutrientDVs = user.getMaximumNutrientDV();
        Map<String, Double> todayNutrientConsumption = user.getTodayNutrientConsumption();
        Map<String, Double> productNutrition = product.getNutriments();
        double productBaseQuantity = product.getBaseQuantity();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        for (String nutrient: Nutrients.nutrientDefaultDV.keySet()) {
            double currNutrientPercentage = (todayNutrientConsumption.get(nutrient) * 100 / maxNutrientDVs.get(nutrient));
            double newNutrientPercentage = currNutrientPercentage;

            String productNutrientKey = nutrient + "_value";
            if (productNutrition.containsKey(productNutrientKey))
                newNutrientPercentage = ((todayNutrientConsumption.get(nutrient) + Math.floor(productNutrition.get(productNutrientKey) * (productQuantity / productBaseQuantity))) * 100 / maxNutrientDVs.get(nutrient));

            String nutrientIncreaseTextViewIdString = "textView_" + nutrient.replace("-", "_") + "_increase";
            Resources res = getResources();
            int nutrientIncreaseTextViewId = res.getIdentifier(nutrientIncreaseTextViewIdString, "id", this.getActivity().getPackageName());

            TextView nutrientIncreaseTextView = content.findViewById(nutrientIncreaseTextViewId);
            nutrientIncreaseTextView.setText(decimalFormat.format(currNutrientPercentage) + "% ->" + decimalFormat.format(newNutrientPercentage) + "%");
        }
    }

    @Override
    public void onDismiss (@NotNull DialogInterface dialog) {

    }
}