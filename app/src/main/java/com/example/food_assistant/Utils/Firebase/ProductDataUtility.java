package com.example.food_assistant.Utils.Firebase;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_assistant.Enums.ProductType;
import com.example.food_assistant.Fragments.ScanProductNutritionalTableRequestFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Utils.Mappers.ProductMapper;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ProductDataUtility {
    private static DatabaseReference mDatabase;

    public static void logProductData(Product product) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        mDatabase.child("products").child(product.getId()).setValue(product);
    }

    public static void getProductById(String productId, ProductSharedViewModel productSharedViewModel) {
        if (mDatabase == null)
            mDatabase = FirebaseDatabase.getInstance("https://foodassistant-43fda-default-rtdb.europe-west1.firebasedatabase.app/").getReference();

        mDatabase.child("products").child(productId).get().addOnCompleteListener(task -> {
            Log.i("productData", task.getResult().toString());
            if (!task.isSuccessful() || task.getResult().getValue() == null) {
                Product product = new Product();
                product.setId(productId);
                product.setProductType(ProductType.CUSTOM);
                productSharedViewModel.select(product);

                /*ScanProductNutritionalTableRequestFragment scanProductNutritionalTableRequestFragment = new ScanProductNutritionalTableRequestFragment();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                scanProductNutritionalTableRequestFragment.show(fragmentManager, "test");*/
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                Gson gson = new Gson();
                JsonElement productElement = gson.toJsonTree(task.getResult().getValue());
                JsonObject productObject = (JsonObject) productElement;
                Product product = ProductMapper.mapFirebaseProduct(productObject);
                product.setProductType(ProductType.CUSTOM);
                productSharedViewModel.select(product);

                /*SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                selectProductQuantityFragment.show(fragmentManager, "test");*/
            }
        });
    }

}
