package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

public class SelectProductQuantityFragment extends DialogFragment {

    private ProductSharedViewModel productSharedViewModel;

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        productSharedViewModel = new ViewModelProvider(requireActivity()).get(ProductSharedViewModel.class);
        View content = inflater.inflate(R.layout.fragment_select_product_quantity, null);
        builder.setView(content)
                .setMessage(R.string.pick_product_quantity)
                .setPositiveButton(R.string.next, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ProductConsumptionEffectsFragment productConsumptionEffectsFragment = new ProductConsumptionEffectsFragment();
                        productConsumptionEffectsFragment.show(getParentFragmentManager(), "test");
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

        Dialog dialog = builder.create();
        TextView measurementUnitTextView = (TextView) content.findViewById(R.id.product_quantity_unit);
        measurementUnitTextView.setText(productSharedViewModel.getSelected().getValue().getMeasurementUnit());

        return dialog;
    }

    @Override
    public void onDismiss (@NotNull DialogInterface dialog) { }
}
