package com.example.food_assistant.Utils.Firebase;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_assistant.Enums.ProductType;
import com.example.food_assistant.Fragments.ScanProductNutritionalTableRequestFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Models.ProductIdentifier;
import com.example.food_assistant.Utils.Mappers.ProductMapper;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class ProductDataUtility {
    private static DatabaseReference mDatabase;
    private static NetworkManager networkManager = NetworkManager.getInstance();

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
            }
            else {
                Log.d("firebase", String.valueOf(task.getResult().getValue()));
                Gson gson = new Gson();
                JsonElement productElement = gson.toJsonTree(task.getResult().getValue());
                JsonObject productObject = (JsonObject) productElement;
                Product product = ProductMapper.mapFirebaseProduct(productObject);
                product.setProductType(ProductType.CUSTOM);
                productSharedViewModel.select(product);
            }
        });
    }

    public static void getProductByIdentifier(ProductIdentifier productIdentifier, ProductSharedViewModel productSharedViewModel) {
        Log.i("INFO", "PRODUCT IDENTIFIER" + productIdentifier);
        ProductType productType = productIdentifier.getProductType();
        String productId = productIdentifier.getId();
        if (productType == ProductType.CUSTOM)
            getProductById(productId, productSharedViewModel);
        else if (productType == ProductType.OPEN_FOOD_FACTS)
            networkManager.getProductDetailsByBarcode(productId, productSharedViewModel);
        else if (productType == ProductType.FOOD_DATA_CENTRAL)
            networkManager.getProductDetailsByFoodDataCentralId(productId, productSharedViewModel);
    }

    public static boolean[] determineIfProductsAreFavoriteForUser(List<ProductIdentifier> productIdentifiers, AppUser appUser) {
        boolean[] areFavorites = new boolean[productIdentifiers.size()];
        List<ProductIdentifier> productFavorites = appUser.getProductFavorites();
        int i = 0;
        for (ProductIdentifier productIdentifier:productIdentifiers) {
            if (productFavorites.contains(productIdentifier))
                areFavorites[i] = true;
            else
                areFavorites[i] = false;
            i++;
        }
        return areFavorites;
    }
}
