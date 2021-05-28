package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_assistant.R;
import com.example.food_assistant.Utils.MLKit.VisionImageProcessor;
import com.example.food_assistant.Utils.ViewModels.ImageProcessorSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import org.jetbrains.annotations.NotNull;

public class SelectProductQuantityFragment extends DialogFragment {

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        ProductSharedViewModel productSharedViewModel = new ViewModelProvider(requireActivity()).get(ProductSharedViewModel.class);
        View content = inflater.inflate(R.layout.fragment_select_product_quantity, null);
        builder.setView(content)
                .setMessage(R.string.pick_product_quantity)
                .setPositiveButton(R.string.next, (dialog, id) -> {

                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // AppUser cancelled the dialog
                   // visionImageProcessor.restart();
                    Bundle result = new Bundle();
                    getParentFragmentManager().setFragmentResult("GET_QUANTITY_CANCEL", result);
                    dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView measurementUnitTextView = content.findViewById(R.id.product_quantity_unit);
        measurementUnitTextView.setText(productSharedViewModel.getSelected().getValue().getMeasurementUnit());

        return dialog;
    }

    @Override
    public void onDismiss (@NotNull DialogInterface dialog) { }

    @Override
    public void onStart()
    {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null)
        {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                TextView productQuantityTextView = dialog.findViewById(R.id.product_quantity);
                String productQuantityString = productQuantityTextView.getText().toString();
                double productQuantity = -1.0;
                try {
                    productQuantity = Double.parseDouble(productQuantityString);
                } catch (Exception e) {
                    Log.i("INFO", "INVALID PROD QUANTITY");
                    productQuantityTextView.setError("Please insert a valid product quantity!");
                }

                if (productQuantity != -1.0) {
                    Bundle result = new Bundle();
                    result.putDouble("productQuantity", productQuantity);
                    getParentFragmentManager().setFragmentResult("GET_QUANTITY_SUCCESS", result);
                    dismiss();
                }

            });
        }
    }

}
