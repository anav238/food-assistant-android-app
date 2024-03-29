package com.example.food_assistant.Utils.MLKit;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;

import com.example.food_assistant.HttpRequest.NetworkManager;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.barcode.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

public class BarcodeScannerProcessor extends VisionProcessorBase<List<Barcode>> {

    private static final String TAG = "BarcodeProcessor";

    private final BarcodeScanner barcodeScanner;

    public BarcodeScannerProcessor(Context context) {
        super(context);
        barcodeScanner = BarcodeScanning.getClient();
    }

    @Override
    public void stop() {
        super.stop();
        barcodeScanner.close();
    }

    @Override
    protected Task<List<Barcode>> detectInImage(InputImage image) {
        return barcodeScanner.process(image);
    }

    @Override
    protected void onSuccess(
            @NonNull List<Barcode> barcodes, ProductSharedViewModel productSharedViewModel) {
        //for (int i = 0; i < barcodes.size(); ++i) {
        if (barcodes.size() > 0) {
            Barcode barcode = barcodes.get(0);
            NetworkManager.getInstance().getProductDetailsByBarcode(barcode.getRawValue(), productSharedViewModel);
            System.out.println(barcode.getRawValue());
            pause();
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }
}


