package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.food_assistant.R;

import org.jetbrains.annotations.NotNull;

public class SelectProductQuantityFragment extends DialogFragment {

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.fragment_select_product_quantity, null))
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
        return builder.create();
    }

    @Override
    public void onDismiss (@NotNull DialogInterface dialog) {
        Context context = getActivity();
        CharSequence text = "Product processed!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
