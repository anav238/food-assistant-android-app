package com.example.food_assistant.Fragments;

import android.app.Dialog;
import android.content.res.Resources;
import android.nfc.FormatException;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.Nutrition.Nutrients;
import com.example.food_assistant.Utils.Firebase.ProductDataUtility;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.MLKit.VisionImageProcessor;
import com.example.food_assistant.Utils.ViewModels.ImageProcessorSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogNewProductFragment extends DialogFragment {

    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;

    public LogNewProductFragment() { }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        productSharedViewModel = new ViewModelProvider(requireActivity()).get(ProductSharedViewModel.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View content = inflater.inflate(R.layout.fragment_log_new_product, null);
        builder.setView(content)
                .setMessage(R.string.log_a_new_product)
                .setPositiveButton("Done", (dialog, id) -> {

                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    Bundle result = new Bundle();
                    getParentFragmentManager().setFragmentResult("ADD_NEW_PRODUCT_TO_DB_CANCEL", result);
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


    @Override
    public void onStart()
    {
        super.onStart();
        AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog == null)
            return;

        Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            try {
                Product product = getProductFromUserInput(dialog);
                boolean isValidProduct = true;
                if (product.getId().length() == 0)
                    isValidProduct = false;

                product.setBaseQuantity(100.0);

                if (isValidProduct) {
                    ProductDataUtility.logProductData(product);
                    CheckBox addToFavorites = dialog.findViewById(R.id.checkBox_add_favorite);
                    if (addToFavorites.isChecked()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AppUser appUser = userSharedViewModel.getSelected().getValue();
                        appUser.addProductFavorite(product);
                        userSharedViewModel.select(appUser);
                        UserDataUtility.updateUserDataToDb(user, userSharedViewModel);
                    }
                    productSharedViewModel.select(product);
                    dismiss();
                }
            } catch (FormatException e) {
                e.printStackTrace();
            }
        });

    }

    private Product getProductFromUserInput(AlertDialog dialog) throws FormatException {
        boolean isValidProduct = true;
        Product product = productSharedViewModel.getSelected().getValue();
        EditText productNameEditText = dialog.findViewById(R.id.editText_product_name);
        if (productNameEditText.getText().length() == 0) {
            productNameEditText.setError("Please enter a product name.");
            isValidProduct = false;
        }
        else
            product.setProductName(productNameEditText.getText().toString());

        product.setMeasurementUnit("grams");

        Map<String, Double> nutrientQuantities = new HashMap<>();
        for (String nutrient: Nutrients.nutrientDefaultDV.keySet()) {
            String editTextIdString = "editText_" + nutrient.replace("-", "_") + "_quantity";
            Resources res = getResources();
            int editTextId = res.getIdentifier(editTextIdString, "id", this.getActivity().getPackageName());

            EditText nutrientQuantityEditText = dialog.findViewById(editTextId);
            try {
                double nutrientQuantity = Double.parseDouble(nutrientQuantityEditText.getText().toString());
                nutrientQuantities.put(nutrient, nutrientQuantity);
            }
            catch (Exception e) {
                nutrientQuantityEditText.setError("Please enter a valid quantity.");
                isValidProduct = false;
            }
        }
        product.setNutriments(nutrientQuantities);
        if (!isValidProduct)
            product.setId("");

        return product;
    }

}