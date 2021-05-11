package com.example.food_assistant.Activities;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory;

import com.example.food_assistant.Fragments.LogNewProductFragment;
import com.example.food_assistant.Utils.Listeners.StartNutritionalTableScanListener;
import com.example.food_assistant.Utils.MLKit.BarcodeScannerProcessor;
import com.example.food_assistant.Utils.MLKit.CameraXViewModel;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.MLKit.TextRecognitionProcessor;
import com.example.food_assistant.Utils.MLKit.VisionImageProcessor;
import com.example.food_assistant.Utils.Firebase.UserDataUtility;
import com.example.food_assistant.Utils.ViewModels.ImageProcessorSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.mlkit.common.MlKitException;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScanProductActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback, StartNutritionalTableScanListener {
    private static final String TAG = "CameraXLivePreview";
    private static final int PERMISSION_REQUESTS = 1;

    private static final String BARCODE_SCANNING = "Barcode Scanning";
    private static final String TEXT_RECOGNITION = "Text Recognition";
    private static final String STATE_SELECTED_MODEL = "selected_model";
    private String selectedModel = BARCODE_SCANNING;

    private PreviewView previewView;
    private UserSharedViewModel userSharedViewModel;
    private ProductSharedViewModel productSharedViewModel;
    private ImageProcessorSharedViewModel imageProcessorSharedViewModel;

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

        productSharedViewModel = new ViewModelProvider(this).get(ProductSharedViewModel.class);
        userSharedViewModel = new ViewModelProvider(this).get(UserSharedViewModel.class);
        imageProcessorSharedViewModel = new ViewModelProvider(this).get(ImageProcessorSharedViewModel.class);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            UserDataUtility.getUserData(user, userSharedViewModel);
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            UserDataUtility.updateUserDataToDb(user, userSharedViewModel);
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
        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }
        if (imageProcessor != null) {
            imageProcessor.stop();
        }

        try {
            if (selectedModel.equals(BARCODE_SCANNING)) {
                Log.i(TAG, "Using Barcode Detector Processor");
                imageProcessor = new BarcodeScannerProcessor(this);
                imageProcessorSharedViewModel.select(imageProcessor);
            }
            else {
                Log.i(TAG, "Using on-device Text recognition Processor");
                imageProcessor = new TextRecognitionProcessor(this);
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
                        imageProcessor.processImageProxy(imageProxy, this);
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

    @Override
    public void onStartNutritionalTableScan() {
        //this.selectedModel = TEXT_RECOGNITION;
        //this.imageProcessor = new TextRecognitionProcessor(this);
        imageProcessor.pause();
        Button scanTextButton = findViewById(R.id.scanTextButton);
        scanTextButton.setVisibility(View.VISIBLE);
        Button cancelScanButton = findViewById(R.id.cancelScanButton);
        cancelScanButton.setVisibility(View.VISIBLE);
    }

    public void scanText(View view) {
        LogNewProductFragment logNewProductFragment = new LogNewProductFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        logNewProductFragment.show(fragmentManager, "test");

        this.selectedModel = BARCODE_SCANNING;
        this.imageProcessor = new BarcodeScannerProcessor(this);
        imageProcessor.pause();

        Button scanTextButton = findViewById(R.id.scanTextButton);
        scanTextButton.setVisibility(View.INVISIBLE);
        Button cancelScanButton = findViewById(R.id.cancelScanButton);
        cancelScanButton.setVisibility(View.INVISIBLE);

        //imageProcessor.restart();
        //imageProcessor.processLatestImage(this);
    }

    public void cancelScanText(View view) {
        this.selectedModel = BARCODE_SCANNING;
        this.imageProcessor = new BarcodeScannerProcessor(this);
        imageProcessor.restart();
        Button scanTextButton = findViewById(R.id.scanTextButton);
        scanTextButton.setVisibility(View.INVISIBLE);
        Button cancelScanButton = findViewById(R.id.cancelScanButton);
        cancelScanButton.setVisibility(View.INVISIBLE);
    }

}