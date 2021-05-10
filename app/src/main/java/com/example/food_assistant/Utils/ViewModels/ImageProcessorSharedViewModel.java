package com.example.food_assistant.Utils.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.food_assistant.Utils.MLKit.VisionImageProcessor;

public class ImageProcessorSharedViewModel extends ViewModel {
    private final MutableLiveData<VisionImageProcessor> selected = new MutableLiveData<VisionImageProcessor>();

    public void select(VisionImageProcessor visionImageProcessor) {
        selected.setValue(visionImageProcessor);
    }

    public LiveData<VisionImageProcessor> getSelected() {
        return selected;
    }
}
