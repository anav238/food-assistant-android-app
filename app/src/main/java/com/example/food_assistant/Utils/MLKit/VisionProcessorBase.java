package com.example.food_assistant.Utils.MLKit;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build.VERSION_CODES;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.vision.common.InputImage;
import java.nio.ByteBuffer;
import java.util.Timer;

public abstract class VisionProcessorBase<T> implements VisionImageProcessor {

    private static final String TAG = "VisionProcessorBase";

    private final Timer fpsTimer = new Timer();
    private final ScopedExecutor executor;

    private boolean isShutdown;
    private boolean isPaused;

    @GuardedBy("this")
    private ByteBuffer latestImage;

    @GuardedBy("this")
    private FrameMetadata latestImageMetaData;

    protected VisionProcessorBase(Context context) {
        executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    }

    private synchronized void processLatestImage(AppCompatActivity activity) {
        ByteBuffer processingImage = latestImage;
        FrameMetadata processingMetaData = latestImageMetaData;
        latestImage = null;
        latestImageMetaData = null;
        if (processingImage != null && processingMetaData != null && !isShutdown && !isPaused) {
            processImage(processingImage, processingMetaData, activity);
        }
    }

    private void processImage(
            ByteBuffer data, final FrameMetadata frameMetadata, AppCompatActivity activity) {

        requestDetectInImage(
                InputImage.fromByteBuffer(
                        data,
                        frameMetadata.getWidth(),
                        frameMetadata.getHeight(),
                        frameMetadata.getRotation(),
                        InputImage.IMAGE_FORMAT_NV21), activity)
                .addOnSuccessListener(executor, results -> processLatestImage(activity));
    }

    @Override
    @RequiresApi(VERSION_CODES.KITKAT)
    @ExperimentalGetImage
    public void processImageProxy(ImageProxy image, AppCompatActivity activity) {
        if (isShutdown || isPaused) {
            image.close();
            return;
        }

        requestDetectInImage(
                InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees()),
                activity)
                .addOnCompleteListener(results -> image.close());
    }

    private Task<T> requestDetectInImage(
            final InputImage image, AppCompatActivity activity) {
        return detectInImage(image)
                .addOnSuccessListener(
                        executor,
                        results -> {
                            VisionProcessorBase.this.onSuccess(results, activity);
                        })
                .addOnFailureListener(
                        executor,
                        e -> {
                            String error = "Failed to process. Error: " + e.getLocalizedMessage();
                            Log.d(TAG, error);
                            e.printStackTrace();
                            VisionProcessorBase.this.onFailure(e);
                        });
    }

    @Override
    public void stop() {
        executor.shutdown();
        isShutdown = true;
        fpsTimer.cancel();
    }

    @Override
    public void pause() {
        isPaused = true;
    }

    @Override
    public void restart() {
        isPaused = false;
    }

    protected abstract Task<T> detectInImage(InputImage image);

    protected abstract void onSuccess(@NonNull T results, AppCompatActivity activity);

    protected abstract void onFailure(@NonNull Exception e);
}