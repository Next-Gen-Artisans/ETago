package com.nextgenartisans.etago.home;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.camera.core.ImageCapture;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tflite.java.TfLite;
import com.nextgenartisans.etago.R;

import org.tensorflow.lite.InterpreterApi;
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

public class DetectionActivity extends AppCompatActivity {

    private static final int INPUT_IMG_SIZE = 768;
    private static final int PIXEL_SIZE = 3;
    private static final int NUM_DETECTIONS = 9;
    private LinearLayout headerContainer;
    private ImageButton backBtn, saveBtn;
    private TextView headerTxt;
    private CardView buttonsCardView;
    private AppCompatImageView detectedImg;
    private LinearLayout buttonsLayout;
    private AppCompatButton censorBtn, cancelBtn;
    private ImageCapture imageCapture;
    private ObjectDetector objectDetector;
    private InterpreterApi interpreter;

    private ByteBuffer modelBuffer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        initializeUI();
        initializeModel();
    }

    private void initializeUI() {
        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // Check if dark mode is active
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                // Dark mode is active, set status bar color accordingly
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            } else {
                // Light mode is active, set status bar color accordingly
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.alt_black));
            }
        }

        // Initialize your views here
        headerContainer = findViewById(R.id.header_container);
        backBtn = findViewById(R.id.back_btn);
        saveBtn = findViewById(R.id.save_btn);
        headerTxt = findViewById(R.id.header_txt);
        buttonsCardView = findViewById(R.id.buttons_cardview);
        detectedImg = findViewById(R.id.detected_img);
        buttonsLayout = findViewById(R.id.buttons_layout);
        censorBtn = findViewById(R.id.censor_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        //View
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();


        // Get the image path from the intent
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("image_path");
        if (imagePath != null && !imagePath.isEmpty()) {
            // Load the image from the path
            Uri imageUri = Uri.parse(imagePath);
            detectedImg.setImageURI(imageUri);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetectionActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetectionActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        censorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetectionActivity.this, "Censoring image...", Toast.LENGTH_SHORT).show();
                detectObjectsFromURI();
            }
        });
    }

    private void initializeModel() {
        try {
            modelBuffer = loadModelFile();
            // Initialize the TFLite interpreter
            Task<Void> initializeTask = TfLite.initialize(getApplicationContext());
            initializeTask.addOnSuccessListener(aVoid -> {
                interpreter = InterpreterApi.create(modelBuffer,
                        new InterpreterApi.Options().setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY));
                Log.i("Interpreter", "Model initialized successfully.");
            }).addOnFailureListener(e -> {
                Log.e("Interpreter", "Failed to initialize model: " + e.getMessage());
            });
        } catch (IOException e) {
            Log.e("Model Initialization", "Failed to load model", e);
        }
    }

    private ByteBuffer loadModelFile() throws IOException {
        AssetManager assetManager = getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd("final_model.tflite");

        try (FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
             FileChannel fileChannel = inputStream.getChannel()) {

            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        }
    }


    // Helper method to convert Bitmap to ByteBuffer (adjust parameters as needed)
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        // Ensure the bitmap is of the expected size or resize it
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 768, 768, true);

        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(768 * 768 * 3 * 4);
        byteBuffer.order(ByteOrder.nativeOrder());

        int[] intValues = new int[768 * 768]; // This now matches the resized bitmap
        // Use the resized bitmap dimensions
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.getWidth(), 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight());

        for (int value : intValues) {
            byteBuffer.putFloat(((value >> 16) & 0xFF) / 255.0f); // Red channel normalized
            byteBuffer.putFloat(((value >> 8) & 0xFF) / 255.0f);  // Green channel normalized
            byteBuffer.putFloat((value & 0xFF) / 255.0f);         // Blue channel normalized
        }
        return byteBuffer;
    }




    private void detectObjectsFromURI() {
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("image_path");
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(this, "No image path available", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri imageUri = Uri.parse(imagePath);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            detectObjects(bitmap);
        } catch (IOException e) {
            Log.e("DetectionActivityLog", "Failed to load image from URI", e);
        }
    }

    private void detectObjects(Bitmap bitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, INPUT_IMG_SIZE, INPUT_IMG_SIZE, true);
        ByteBuffer inputBuffer = convertBitmapToByteBuffer(resizedBitmap);

        float[][][] detectionOutput = new float[1][9][12096];

        Map<Integer, Object> outputMap = new HashMap<>();
        outputMap.put(0, detectionOutput);

        interpreter.runForMultipleInputsOutputs(new Object[]{inputBuffer}, outputMap);
        Log.i("DetectionActivityLog", "Model Inference was a success!");
        //Process the detection output outputMap

    }

}