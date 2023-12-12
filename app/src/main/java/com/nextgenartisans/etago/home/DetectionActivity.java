package com.nextgenartisans.etago.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nextgenartisans.etago.R;

public class DetectionActivity extends AppCompatActivity {

    private LinearLayout headerContainer;
    private ImageButton backBtn, saveBtn;
    private TextView headerTxt;
    private CardView buttonsCardView;
    private AppCompatImageView detectedImg;
    private LinearLayout buttonsLayout;
    private AppCompatButton censorBtn, cancelBtn;

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


        // Get the image path from the intent
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("image_path");
        if (imagePath != null && !imagePath.isEmpty()) {
            // Load the image from the path
            Uri imageUri = Uri.parse(imagePath);
            detectedImg.setImageURI(imageUri);
        }

    }
}