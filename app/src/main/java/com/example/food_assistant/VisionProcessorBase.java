package com.example.food_assistant;

import android.content.Context;
import android.os.Build.VERSION_CODES;
import androidx.annotation.GuardedBy;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.util.Log;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageProxy;
import androidx.fragment.app.FragmentManager;

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

    @GuardedBy("this")
    private ByteBuffer latestImage;

    @GuardedBy("this")
    private FrameMetadata latestImageMetaData;

    protected VisionProcessorBase(Context context) {
        executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    }

    private synchronized void processLatestImage(FragmentManager fragmentManager) {
        ByteBuffer processingImage = latestImage;
        FrameMetadata processingMetaData = latestImageMetaData;
        latestImage = null;
        latestImageMetaData = null;
        if (processingImage != null && processingMetaData != null && !isShutdown) {
            processImage(processingImage, processingMetaData, fragmentManager);
        }
    }

    private void processImage(
            ByteBuffer data, final FrameMetadata frameMetadata, FragmentManager fragmentManager) {

        requestDetectInImage(
                InputImage.fromByteBuffer(
                        data,
                        frameMetadata.getWidth(),
                        frameMetadata.getHeight(),
                        frameMetadata.getRotation(),
                        InputImage.IMAGE_FORMAT_NV21), fragmentManager)
                .addOnSuccessListener(executor, results -> processLatestImage(fragmentManager));
    }

    @Override
    @RequiresApi(VERSION_CODES.KITKAT)
    @ExperimentalGetImage
    public void processImageProxy(ImageProxy image, FragmentManager fragmentManager) {
        if (isShutdown) {
            image.close();
            return;
        }

        requestDetectInImage(
                InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees()),
                fragmentManager)
                .addOnCompleteListener(results -> image.close());
    }

    private Task<T> requestDetectInImage(
            final InputImage image, FragmentManager fragmentManager) {
        return detectInImage(image)
                .addOnSuccessListener(
                        executor,
                        results -> {
                            VisionProcessorBase.this.onSuccess(results, fragmentManager);
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

    protected abstract Task<T> detectInImage(InputImage image);

    protected abstract void onSuccess(@NonNull T results, FragmentManager fragmentManager);

    protected abstract void onFailure(@NonNull Exception e);
}