package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Nutrition.NutrientCalculator;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ProductInfoFragment extends DialogFragment {

    public ProductInfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NotNull
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View view = inflater.inflate(R.layout.fragment_product_info, null);

        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        UserSharedViewModel userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        ProductSharedViewModel productSharedViewModel = new ViewModelProvider(requireActivity()).get(ProductSharedViewModel.class);

        AppUser appUser = userSharedViewModel.getSelected().getValue();
        Product product = productSharedViewModel.getSelected().getValue();

        if (product != null) {
            TextView productNameTextView = view.findViewById(R.id.textView_product_name);
            productNameTextView.setText(product.getProductName());

            TextView nutriScoreGradeTextView = view.findViewById(R.id.textView_nutriscore_grade);
            nutriScoreGradeTextView.setText(product.getNutriScoreGrade());

            TextView novaScoreGradeTextView = view.findViewById(R.id.textView_novascore_grade);
            novaScoreGradeTextView.setText(product.getNovaGroup());

            setupProductNutritionFragment(appUser, product);

            Button closeButton = view.findViewById(R.id.button_close);
            closeButton.setOnClickListener(v -> dismiss());

            Button logProductButton = view.findViewById(R.id.button_log);
            logProductButton.setOnClickListener(v -> {
                Bundle result = new Bundle();
                getParentFragmentManager().setFragmentResult("LOG_PRODUCT_REQUEST", result);
                dismiss();
            });
        }

        return view;
    }

    private void setupProductNutritionFragment(AppUser appUser, Product product) {
        System.out.println(product);
        Map<String, Double> nutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(product.getNutriments(), appUser);

        Bundle bundle = new Bundle();
        bundle.putStringArray("nutrients", nutrientPercentages.keySet().toArray(new String[nutrientPercentages.keySet().size()]));
        for (String nutrient:nutrientPercentages.keySet())
            bundle.putInt(nutrient, (int) Math.round(nutrientPercentages.get(nutrient)));

        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag("productNutrientIntake");

        if (fragment == null) {
            fragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_nutritional_values, NutrientIntakeFragment.class, bundle, "productNutrientIntake")
                    .commit();
        }
        else
            fragmentManager.beginTransaction().replace(R.id.fragment_nutritional_values, NutrientIntakeFragment.class, bundle, "productNutrientIntake").commit();
    }


}