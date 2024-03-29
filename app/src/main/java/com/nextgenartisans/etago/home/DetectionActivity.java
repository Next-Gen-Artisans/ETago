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

    private static final int BATCH_SIZE = 1;
    private static final int INPUT_IMG_SIZE = 768;
    private static final int PIXEL_SIZE = 3;
    private static final float DETECTION_THRESHOLD = 0.5f;
    private static final int NUM_DETECTIONS = 10;
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

    private final ByteBuffer modelBuffer = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the model buffer
        ByteBuffer modelBuffer;
        try {
            modelBuffer = loadModelFile();
            // Proceed with creating the Interpreter
            Task<Void> initializeTask = TfLite.initialize(getApplicationContext());
            initializeTask.addOnSuccessListener(aVoid -> {
                interpreter = InterpreterApi.create(modelBuffer,
                        new InterpreterApi.Options().setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY));
                // Use the interpreter for inference
                setUpClickListeners();
            }).addOnFailureListener(e -> {
                Log.e("Interpreter", "Cannot initialize interpreter: " + e.getMessage());
            });
        } catch (IOException e) {
            Log.e("DetectObjects", "Failed to load model", e);
            return; // Return or handle the error appropriately
        }

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

        setContentView(R.layout.activity_detection);

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


    }

    private void setUpClickListeners() {
        censorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectObjects();
            }
        });
    }

    private ByteBuffer loadModelFile() throws IOException {
        AssetManager assetManager = getAssets();
        AssetFileDescriptor fileDescriptor = assetManager.openFd("e_tago_32.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    // Helper method to convert Bitmap to ByteBuffer (adjust parameters as needed)
    private ByteBuffer convertBitmapToByteBuffer(Bitmap bitmap) {
        // Adjust the buffer size for 'uint8' type (1 byte per pixel channel).
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(INPUT_IMG_SIZE * INPUT_IMG_SIZE * PIXEL_SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());

        // Dynamically allocate the intValues array based on the bitmap's dimensions
        int[] intValues = new int[bitmap.getWidth() * bitmap.getHeight()];

        // Ensure the bitmap is of the expected size or resized before this step
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int value : intValues) {
            byteBuffer.put((byte) ((value >> 16) & 0xFF)); // Red channel
            byteBuffer.put((byte) ((value >> 8) & 0xFF));  // Green channel
            byteBuffer.put((byte) (value & 0xFF));         // Blue channel
        }
        return byteBuffer;
    }

    private void detectObjects() {
        // Get the image path from the intent (captured image path)
        Intent intentFromCaptureImg = getIntent();
        String imagePathFromCaptureImg = intentFromCaptureImg.getStringExtra("image_path");
        if (imagePathFromCaptureImg == null || imagePathFromCaptureImg.isEmpty()) {
            Toast.makeText(this, "No image path available", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri imageUri = Uri.parse(imagePathFromCaptureImg);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // Assuming your model takes a 300x300 pixel image as input
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 768, 1024, true);
            ByteBuffer inputBuffer = convertBitmapToByteBuffer(resizedBitmap);

            // Adjust the size of this array based on your model's output
            float[][][] outputLocations = new float[1][NUM_DETECTIONS][4]; // Example for bounding box output
            float[][] outputClasses = new float[1][NUM_DETECTIONS]; // Class labels
            float[][] outputScores = new float[1][NUM_DETECTIONS]; // Confidence scores
            float[] numDetections = new float[1]; // Number of detections

            Object[] inputArray = {inputBuffer};
            Map<Integer, Object> outputMap = new HashMap<>();
            outputMap.put(0, outputLocations);
            outputMap.put(1, outputClasses);
            outputMap.put(2, outputScores);
            outputMap.put(3, numDetections);

            interpreter.runForMultipleInputsOutputs(inputArray, outputMap);

            // Process the output here. For example, drawing bounding boxes based on `outputLocations`.
            // The processing part will depend on how you want to use the detection results.
        } catch (IOException e) {
            Log.e("DetectObjects", "Failed to load image", e);
        }
    }



}