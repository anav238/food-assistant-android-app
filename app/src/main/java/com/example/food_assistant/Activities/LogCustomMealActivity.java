package com.example.food_assistant.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ActivityResultContracts.GetBrandedProduct;
import com.example.food_assistant.Utils.ActivityResultContracts.GetGenericProduct;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LogCustomMealActivity extends AppCompatActivity {

    private boolean fabExpanded = false;
    private LinearLayout layoutFabScan;
    private LinearLayout layoutFabLog;
    private FloatingActionButton fabAdd;
    private ActivityResultLauncher<String> openScanProduct;
    private ActivityResultLauncher<String> openLogGenericFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_custom_meal);
        layoutFabScan = findViewById(R.id.linearLayout_scan_product);
        layoutFabLog = findViewById(R.id.linearLayout_add_generic_food);
        fabAdd = findViewById(R.id.fab_add);

        openScanProduct = registerForActivityResult(new GetBrandedProduct(),
                product -> {
                    if (product != null) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), "Product added!", duration);
                        toast.show();
                    }
                });

        openLogGenericFood = registerForActivityResult(new GetGenericProduct(),
                product -> {
                    if (product != null) {
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(getApplicationContext(), "Product added!", duration);
                        toast.show();
                    }
                });

    }

    public void toggleSubMenusFab(View view) {
        if (!fabExpanded)
            openSubMenusFab();
        else
            closeSubMenusFab();
    }

    private void closeSubMenusFab(){
        layoutFabScan.setVisibility(View.INVISIBLE);
        layoutFabLog.setVisibility(View.INVISIBLE);
        fabAdd.setImageResource(R.drawable.ic_plus);
        fabExpanded = false;
    }

    private void openSubMenusFab(){
        layoutFabScan.setVisibility(View.VISIBLE);
        layoutFabLog.setVisibility(View.VISIBLE);
        fabAdd.setImageResource(R.drawable.ic_close);
        fabExpanded = true;
    }

    public void openScanProductActivity(View view) {
        openScanProduct.launch("Single Scan Mode");
        closeSubMenusFab();
    }

    public void openLogGenericFoodActivity(View view) {
        openLogGenericFood.launch("Single Log Mode");
        closeSubMenusFab();
    }

}