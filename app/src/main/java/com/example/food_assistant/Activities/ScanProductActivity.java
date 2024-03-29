package com.example.food_assistant.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;

import com.example.food_assistant.Fragments.LogNewProductFragment;
import com.example.food_assistant.Fragments.ProductConsumptionEffectsFragment;
import com.example.food_assistant.Fragments.ScanProductNutritionalTableRequestFragment;
import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.Models.AppDataManager;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Utils.MLKit.BarcodeScannerProcessor;
import com.example.food_assistant.Utils.MLKit.CameraXViewModel;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.MLKit.VisionImageProcessor;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.Nutrition.NutrientCalculator;
import com.example.food_assistant.Utils.ViewModels.ImageProcessorSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.common.MlKitException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScanProductActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback {
    private static final String TAG = "CameraXLivePreview";
    private static final int PERMISSION_REQUESTS = 1;

    private static final String BARCODE_SCANNING = "Barcode Scanning";
    private static final String SINGLE_SCAN_MODE = "Single Scan Mode";
    private static final String MULTIPLE_SCAN_MODE = "Multiple Scan Mode";

    private static final String STATE_SELECTED_MODEL = "selected_model";
    private static final String STATE_SELECTED_SCAN_MODE = "selected_scan_mode";

    private String selectedModel = BARCODE_SCANNING;
    private String selectedScanMode = MULTIPLE_SCAN_MODE;

    private PreviewView previewView;
    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;
    private ImageProcessorSharedViewModel imageProcessorSharedViewModel;

    private AppDataManager appDataManager;

    @Nullable private ProcessCameraProvider cameraProvider;
    @Nullable private Preview previewUseCase;
    @Nullable private ImageAnalysis analysisUseCase;
    @Nullable private VisionImageProcessor imageProcessor;

    private CameraSelector cameraSelector;

    public void closeActivity(View view) {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int lensFacing = CameraSelector.LENS_FACING_BACK;
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
        if (savedInstanceState != null) {
            selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, BARCODE_SCANNING);
            selectedScanMode = savedInstanceState.getString(STATE_SELECTED_SCAN_MODE, MULTIPLE_SCAN_MODE);
        }
        else {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey(STATE_SELECTED_SCAN_MODE))
                selectedScanMode = bundle.getString(STATE_SELECTED_SCAN_MODE);
        }

        setContentView(R.layout.activity_scan_product);
        previewView = findViewById(R.id.previewView);

        new ViewModelProvider(this, AndroidViewModelFactory.getInstance(getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        this,
                        provider -> {
                            cameraProvider = provider;
                            if (allPermissionsGranted()) {
                                bindAllCameraUseCases();
                            }
                        });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }

        setupFragmentResultListeners();

        appDataManager = AppDataManager.getInstance();
        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        imageProcessorSharedViewModel = new ViewModelProvider(this).get(ImageProcessorSharedViewModel.class);

        productSharedViewModel.getSelected().observe(this, product -> {
            Log.i("INFO", "Scanned product" + product.toString());
            processScannedProduct(product);
        });
    }

    private void setupFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener("GET_QUANTITY_SUCCESS", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                double productQuantity = bundle.getDouble("productQuantity");
                Product currentProduct = productSharedViewModel.getSelected().getValue();
                AppUser currentUser = appDataManager.getAppUser();

                Map<String, Double> initialNutrientValues = currentUser.getTodayNutrientConsumption();
                Map<String, Double> initialNutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(initialNutrientValues, currentUser);
                Map<String, Double> productNutrientValues = currentProduct.getNutriments();

                Map<String, Double> totalNutrientValues = NutrientCalculator.addProductNutritionToUserDailyNutrition(initialNutrientValues, productNutrientValues, productQuantity);
                Map<String, Double> totalNutrientPercentages = NutrientCalculator.getNutrientsPercentageFromMaximumDV(totalNutrientValues, currentUser);

                Bundle newFragmentBundle = new Bundle();
                newFragmentBundle.putDouble("productQuantity", productQuantity);
                newFragmentBundle.putStringArray("nutrients", totalNutrientPercentages.keySet().toArray(new String[totalNutrientPercentages.keySet().size()]));

                for (String nutrient:initialNutrientPercentages.keySet())
                    newFragmentBundle.putDouble(nutrient + "_initial", initialNutrientPercentages.get(nutrient));

                for (String nutrient:totalNutrientPercentages.keySet())
                    newFragmentBundle.putDouble(nutrient + "_final", totalNutrientPercentages.get(nutrient));

                ProductConsumptionEffectsFragment productConsumptionEffectsFragment = new ProductConsumptionEffectsFragment();
                productConsumptionEffectsFragment.setArguments(newFragmentBundle);
                productConsumptionEffectsFragment.show(getSupportFragmentManager(), "test");
            }
        });

        getSupportFragmentManager().setFragmentResultListener("GET_QUANTITY_CANCEL", this, (requestKey, bundle) -> imageProcessor.restart());

        getSupportFragmentManager().setFragmentResultListener("PROCESS_PRODUCT_SUCCESS", this, (requestKey, bundle) -> {
            double productQuantity = bundle.getDouble("productQuantity");
            AppUser user = appDataManager.getAppUser();
            Product product = productSharedViewModel.getSelected().getValue();
            user.updateUserNutrientConsumptionWithProduct(product, productQuantity);
            appDataManager.setAppUser(user);
            userSharedViewModel.select(user);

            UserDataUtility.updateUserDataToDb(FirebaseAuth.getInstance().getCurrentUser(), userSharedViewModel);

            CharSequence text = "Product logged!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            if (imageProcessor != null)
                imageProcessor.restart();
        });

        getSupportFragmentManager().setFragmentResultListener("PROCESS_PRODUCT_CANCEL",
                this, (requestKey, bundle) -> {
                    if (imageProcessor != null)
                        imageProcessor.restart();
                });


        getSupportFragmentManager().setFragmentResultListener("ADD_NEW_PRODUCT_TO_DB_REQUEST_SUCCESS",
                this, (requestKey, bundle) -> {
            LogNewProductFragment logNewProductFragment = new LogNewProductFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            logNewProductFragment.show(fragmentManager, "test");
        });

        getSupportFragmentManager().setFragmentResultListener("ADD_NEW_PRODUCT_TO_DB_REQUEST_CANCEL",
                this, (requestKey, bundle) -> {
                    if (imageProcessor != null)
                        imageProcessor.restart();
                });

        getSupportFragmentManager().setFragmentResultListener("ADD_NEW_PRODUCT_TO_DB_CANCEL",
                this, (requestKey, bundle) -> {
                    if (imageProcessor != null)
                        imageProcessor.restart();
                });
    }

    private void processScannedProduct(Product product) {
        if (product.getNutriments() == null || product.getNutriments().size() == 0) {
            ScanProductNutritionalTableRequestFragment scanProductNutritionalTableRequestFragment = new ScanProductNutritionalTableRequestFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            scanProductNutritionalTableRequestFragment.show(fragmentManager, "test");
        }
        else {
            if (selectedScanMode.equals(MULTIPLE_SCAN_MODE)) {
                SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
                FragmentManager fragmentManager = getSupportFragmentManager();
                selectProductQuantityFragment.show(fragmentManager, "test");
            }
            else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("product", product);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        }
    }



    @Override
    public void onResume() {
        super.onResume();
        bindAllCameraUseCases();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imageProcessor != null)
            imageProcessor.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
    }

    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        Size targetResolution = new Size(1280, 800);
        builder.setTargetResolution(targetResolution);

        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle( this, cameraSelector, previewUseCase);
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null)
            return;

        if (analysisUseCase != null)
            cameraProvider.unbind(analysisUseCase);

        if (imageProcessor != null)
            imageProcessor.stop();

        try {
            if (selectedModel.equals(BARCODE_SCANNING)) {
                Log.i(TAG, "Using Barcode Detector Processor");
                imageProcessor = new BarcodeScannerProcessor(this);
                imageProcessorSharedViewModel.select(imageProcessor);
            }

        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: " + BARCODE_SCANNING, e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        Size targetResolution = new Size(1280, 800);
        builder.setTargetResolution(targetResolution);

        analysisUseCase = builder.build();

        analysisUseCase.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(this),
                imageProxy -> {
                    try {
                        imageProcessor.processImageProxy(imageProxy, productSharedViewModel);
                    } catch (MlKitException e) {
                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        cameraProvider.bindToLifecycle(this, cameraSelector, analysisUseCase);
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            bindAllCameraUseCases();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }


}