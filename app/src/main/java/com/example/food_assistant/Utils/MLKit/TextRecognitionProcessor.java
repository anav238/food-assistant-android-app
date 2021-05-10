package com.example.food_assistant.Utils.MLKit;

import android.content.Context;
import android.graphics.Point;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.Text.Element;
import com.google.mlkit.vision.text.Text.Line;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import java.util.List;

public class TextRecognitionProcessor extends VisionProcessorBase<Text> {

    private static final String TAG = "TextRecProcessor";

    private final TextRecognizer textRecognizer;

    public TextRecognitionProcessor(Context context) {
        super(context);
        textRecognizer = TextRecognition.getClient();
    }

    @Override
    public void stop() {
        super.stop();
        textRecognizer.close();
    }

    @Override
    protected Task<Text> detectInImage(InputImage image) {
        return textRecognizer.process(image);
    }

    @Override
    protected void onSuccess(@NonNull Text text, AppCompatActivity activity) {
        Log.d(TAG, "On-device Text detection successful");
        logExtrasForTesting(text);
    }

    private static void logExtrasForTesting(Text text) {
        if (text != null) {
            Log.v("TEXT_DETECTOR", "Detected text has : " + text.getTextBlocks().size() + " blocks");
            for (int i = 0; i < text.getTextBlocks().size(); ++i) {
                List<Line> lines = text.getTextBlocks().get(i).getLines();
                Log.v(
                        "TEXT_DETECTOR",
                        String.format("Detected text block %d has %d lines", i, lines.size()));
                for (int j = 0; j < lines.size(); ++j) {
                    List<Element> elements = lines.get(j).getElements();
                    Log.v(
                            "TEXT_DETECTOR",
                            String.format("Detected text line %d has %d elements", j, elements.size()));
                    for (int k = 0; k < elements.size(); ++k) {
                        Element element = elements.get(k);
                        Log.v(
                                "TEXT_DETECTOR",
                                String.format("Detected text element %d says: %s", k, element.getText()));
                        Log.v(
                                "TEXT_DETECTOR",
                                String.format(
                                        "Detected text element %d has a bounding box: %s",
                                        k, element.getBoundingBox().flattenToString()));
                        Log.v(
                                "TEXT_DETECTOR",
                                String.format(
                                        "Expected corner point size is 4, get %d", element.getCornerPoints().length));
                        for (Point point : element.getCornerPoints()) {
                            Log.v(
                                    "TEXT_DETECTOR",
                                    String.format(
                                            "Corner point for element %d is located at: x - %d, y = %d",
                                            k, point.x, point.y));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.w(TAG, "Text detection failed." + e);
    }
}