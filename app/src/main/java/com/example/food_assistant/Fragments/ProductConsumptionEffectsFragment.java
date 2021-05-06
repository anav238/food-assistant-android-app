package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;

import org.jetbrains.annotations.NotNull;

public class ProductConsumptionEffectsFragment extends DialogFragment {

    private ProductSharedViewModel productSharedViewModel;

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        productSharedViewModel = new ViewModelProvider(requireActivity()).get(ProductSharedViewModel.class);
        Product product = productSharedViewModel.getSelected().getValue();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View content = inflater.inflate(R.layout.fragment_product_consumption_effects, null);
        builder.setView(content)
                .setMessage("Are you sure you want to consume this product?")
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // AppUser cancelled the dialog
                    }
                });

        TextView nutriScoreGradeTextView = (TextView) content.findViewById(R.id.nutriScoreGrade);
        nutriScoreGradeTextView.setText(product.getNutriScoreGrade());

        TextView novaScoreGradeTextView = (TextView) content.findViewById(R.id.novaScoreGrade);
        novaScoreGradeTextView.setText(product.getNovaGroup());

        return builder.create();
    }

    @Override
    public void onDismiss (@NotNull DialogInterface dialog) {
        Context context = getActivity();
        CharSequence text = "Product logged!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}