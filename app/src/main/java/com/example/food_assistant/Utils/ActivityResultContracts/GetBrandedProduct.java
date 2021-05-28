package com.example.food_assistant.Utils.ActivityResultContracts;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.food_assistant.Activities.MainActivity;
import com.example.food_assistant.Activities.ScanProductActivity;
import com.example.food_assistant.Models.Product;

public class GetBrandedProduct extends ActivityResultContract<String, Product> {
    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, String scanMode) {
        Intent intent = new Intent(context, ScanProductActivity.class);
        intent.putExtra("selected_scan_mode", scanMode);
        return intent;
    }

    @Override
    public Product parseResult(int resultCode, @Nullable Intent result) {
        if (resultCode != Activity.RESULT_OK || result == null) {
            return null;
        }
        return (Product) result.getSerializableExtra("product");
    }
}
