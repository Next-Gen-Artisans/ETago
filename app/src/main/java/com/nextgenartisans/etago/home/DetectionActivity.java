package com.nextgenartisans.etago.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.nextgenartisans.etago.R;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.task.gms.vision.detector.Detection;
import org.tensorflow.lite.task.gms.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.List;

public class DetectionActivity extends AppCompatActivity {

    private LinearLayout headerContainer;
    private ImageButton backBtn, saveBtn;
    private TextView headerTxt;
    private CardView buttonsCardView;
    private AppCompatImageView detectedImg;
    private LinearLayout buttonsLayout;
    private AppCompatButton censorBtn, cancelBtn;

    private ImageCapture imageCapture;

    private ObjectDetector objectDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                Intent i = new Intent(DetectionActivity.this, CaptureImg.class);
                startActivity(i);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetectionActivity.this, CaptureImg.class);
                startActivity(i);
                finish();
            }
        });

        // Initialize ObjectDetector
        try {
            objectDetector = ObjectDetector.createFromFile(this, "app/src/main/assets/e_tago_32.tflite");
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load model", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return; // Stop the activity initialization if the model cannot be loaded
        }

        censorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectObjects();
            }
        });


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
            TensorImage tensorImage = TensorImage.fromBitmap(bitmap);
            List<Detection> results = objectDetector.detect(tensorImage);

            // Here, you can process the results, for example, draw bounding boxes on the image.
            // However, for simplicity, we're just showing a toast with the number of detected objects.
            Toast.makeText(this, ((List<?>) results).size() + " objects detected.", Toast.LENGTH_LONG).show();

            // You would then proceed to the censoring part with the detected bounding boxes
            // For now, we are just starting the CensorActivity as before
            Intent intentToCensorActivity = new Intent(DetectionActivity.this, CensorActivity.class);
            intentToCensorActivity.putExtra("censored_image_path", imagePathFromCaptureImg);
            startActivity(intentToCensorActivity);
            finish();

        } catch (IOException e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



}