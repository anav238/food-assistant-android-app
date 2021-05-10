package com.example.food_assistant.Utils.MLKit;


import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.common.MlKitException;

public interface VisionImageProcessor {
    /** Processes ImageProxy image data, e.g. used for CameraX live preview case. */
    @RequiresApi(VERSION_CODES.KITKAT)
    void processImageProxy(ImageProxy image, AppCompatActivity activity) throws MlKitException;

    /** Stops the underlying machine learning model and release resources. */
    void stop();

    /** pauses the execution */
    void pause();

    /** restarts the execution */
    void restart();
}