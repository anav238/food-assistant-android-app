package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Constants.Nutrients;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.Map;

public class ProductConsumptionEffectsFragment extends DialogFragment {

    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;

    @NotNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        productSharedViewModel = new ViewModelProvider(requireActivity()).get(ProductSharedViewModel.class);

        AppUser user = userSharedViewModel.getSelected().getValue();
        Product product = productSharedViewModel.getSelected().getValue();
        Double productQuantity = requireArguments().getDouble("productQuantity");
        Log.i("PROD_QUANTITY", String.valueOf(productQuantity));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.fragment_product_consumption_effects, null);
        builder.setView(content)
                .setMessage("Are you sure you want to consume this product?")
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                        Context context = getActivity();
                        CharSequence text = "Product logged!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // AppUser cancelled the dialog
                    }
                });

        TextView nutriScoreGradeTextView = content.findViewById(R.id.nutriScoreGrade);
        nutriScoreGradeTextView.setText(product.getNutriScoreGrade());

        TextView novaScoreGradeTextView = content.findViewById(R.id.novaScoreGrade);
        novaScoreGradeTextView.setText(product.getNovaGroup());

        Map<String, Double> maxNutrientDVs = user.getMaximumNutrientDV();
        Map<String, Double> todayNutrientConsumption = user.getTodayNutrientConsumption();
        Map<String, Double> productNutrition = product.getNutriments();
        Double productBaseQuantity = product.getBaseQuantity();
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        for (String nutrient: Nutrients.nutrientDefaultDV.keySet()) {
            double currNutrientPercentage = (todayNutrientConsumption.get(nutrient) * 100 / maxNutrientDVs.get(nutrient));
            double newNutrientPercentage = currNutrientPercentage;

            String productNutrientKey = nutrient + "_value";
            Log.i("TEST", productNutrientKey);
            Log.i("TEST_PROD_NUTR", productNutrition.toString());
            if (productNutrition.containsKey(productNutrientKey))
                Log.i("PROD_NUTRITION", String.valueOf(productNutrition.get(productNutrientKey)));
            if (productNutrition.containsKey(productNutrientKey))
                newNutrientPercentage = ((todayNutrientConsumption.get(nutrient) + Math.floor(productNutrition.get(productNutrientKey) * (productQuantity / productBaseQuantity))) * 100 / maxNutrientDVs.get(nutrient));

            String nutrientIncreaseTextViewIdString = "textView_" + nutrient.replace("-", "_") + "_increase";
            Resources res = getResources();
            int nutrientIncreaseTextViewId = res.getIdentifier(nutrientIncreaseTextViewIdString, "id", this.getActivity().getPackageName());

            TextView nutrientIncreaseTextView = content.findViewById(nutrientIncreaseTextViewId);
            nutrientIncreaseTextView.setText(decimalFormat.format(currNutrientPercentage) + "% ->" + decimalFormat.format(newNutrientPercentage) + "%");
        }
        return builder.create();
    }

    @Override
    public void onDismiss (@NotNull DialogInterface dialog) {

    }
}