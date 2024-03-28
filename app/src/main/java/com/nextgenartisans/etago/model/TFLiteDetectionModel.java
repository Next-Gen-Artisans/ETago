package com.nextgenartisans.etago.model;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;

import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.support.common.FileUtil;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class TFLiteDetectionModel {

    private InterpreterApi tflite;
    private List<String> labels;

    public TFLiteDetectionModel(Context context, String modelPath, String labelsPath) {
        try {
            // Load and initialize the TFLite model
            MappedByteBuffer modelFile = loadModelFile(context.getAssets(), modelPath);
            tflite = InterpreterApi.create(FileUtil.loadMappedFile(context, modelPath), new InterpreterApi.Options());
            // Load labels
            labels = loadLabels(context.getAssets(), labelsPath);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exceptions here
        }
    }

    private MappedByteBuffer loadModelFile(AssetManager assetManager, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = assetManager.openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Method to load labels from classes.txt
    private List<String> loadLabels(AssetManager assets, String labelsPath) throws IOException {
        List<String> labelList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(assets.open(labelsPath)));
        String line;
        while ((line = reader.readLine()) != null) {
            labelList.add(line);
        }
        reader.close();
        return labelList;
    }

    // Methods for running inference and processing results go here
    public void runInference(Object input, Object output) {
        if (tflite != null) {
            tflite.run(input, output);
        }
    }

    public void close() {
        if (tflite != null) {
            tflite.close();
        }
    }



}
