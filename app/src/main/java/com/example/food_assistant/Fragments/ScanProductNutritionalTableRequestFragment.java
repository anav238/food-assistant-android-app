package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Listeners.StartNutritionalTableScanListener;
import com.example.food_assistant.Utils.MLKit.VisionImageProcessor;
import com.example.food_assistant.Utils.ViewModels.ImageProcessorSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import org.jetbrains.annotations.NotNull;

public class ScanProductNutritionalTableRequestFragment extends DialogFragment {

    // 1. Defines the listener interface with a method passing back data result.

    public ScanProductNutritionalTableRequestFragment() {
        // Required empty public constructor
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        ImageProcessorSharedViewModel imageProcessorSharedViewModel = new ViewModelProvider(requireActivity()).get(ImageProcessorSharedViewModel.class);

        VisionImageProcessor visionImageProcessor = imageProcessorSharedViewModel.getSelected().getValue();

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View content = inflater.inflate(R.layout.fragment_scan_product_nutritional_table_request, null);
        builder.setView(content)
                .setMessage(R.string.scan_product_nutritional_table_request)
                .setPositiveButton("Yes", (dialog, id) -> {
                    StartNutritionalTableScanListener listener = (StartNutritionalTableScanListener) getActivity();
                    if (listener != null)
                        listener.onStartNutritionalTableScan();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    // AppUser cancelled the dialog
                    visionImageProcessor.restart();
                });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scan_product_nutritional_table_request, container, false);
    }
}