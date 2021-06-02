package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_assistant.R;
import com.example.food_assistant.Utils.MLKit.VisionImageProcessor;
import com.example.food_assistant.Utils.ViewModels.ImageProcessorSharedViewModel;

import org.jetbrains.annotations.NotNull;

public class ScanProductNutritionalTableRequestFragment extends DialogFragment {

    public ScanProductNutritionalTableRequestFragment() { }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View content = inflater.inflate(R.layout.fragment_scan_product_nutritional_table_request, null);
        builder.setView(content)
                .setMessage(R.string.scan_product_nutritional_table_request)
                .setPositiveButton("Yes", (dialog, id) -> {
                    Bundle result = new Bundle();
                    getParentFragmentManager().setFragmentResult("ADD_NEW_PRODUCT_TO_DB_REQUEST_SUCCESS", result);
                })
                .setNegativeButton("No", (dialog, id) -> {
                    Bundle result = new Bundle();
                    getParentFragmentManager().setFragmentResult("ADD_NEW_PRODUCT_TO_DB_REQUEST_CANCEL", result);
                    dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan_product_nutritional_table_request, container, false);
    }
}