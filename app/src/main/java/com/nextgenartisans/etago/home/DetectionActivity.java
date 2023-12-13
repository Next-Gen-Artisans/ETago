package com.nextgenartisans.etago.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nextgenartisans.etago.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DetectionActivity extends AppCompatActivity {

    private LinearLayout headerContainer;
    private ImageButton backBtn, saveBtn;
    private TextView headerTxt;
    private CardView buttonsCardView;
    private AppCompatImageView detectedImg;
    private LinearLayout buttonsLayout;
    private AppCompatButton censorBtn, cancelBtn;

    private ImageCapture imageCapture;

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

        censorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the image path from the intent (captured image path)
                Intent intentFromCaptureImg = getIntent();
                String imagePathFromCaptureImg = intentFromCaptureImg.getStringExtra("image_path");

                if (imagePathFromCaptureImg != null && !imagePathFromCaptureImg.isEmpty()) {
                    // Create an intent to start CensorActivity
                    Intent intentToCensorActivity = new Intent(DetectionActivity.this, CensorActivity.class);
                    // Pass the image file path as an extra
                    intentToCensorActivity.putExtra("censored_image_path", imagePathFromCaptureImg);
                    intentToCensorActivity.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intentToCensorActivity);
                    finish();
                } else {
                    Toast.makeText(DetectionActivity.this, "No image path available", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }



}