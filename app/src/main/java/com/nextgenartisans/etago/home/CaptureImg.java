package com.nextgenartisans.etago.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nextgenartisans.etago.R;

public class CaptureImg extends AppCompatActivity {

    // Declare member variables for each view
    private CardView buttonsCardView;
    private LinearLayout uploadImageContainer, headerContainer;
    private ImageButton backBtn, saveBtn;
    private TextView headerTxt;
    private AppCompatImageView uploadedImg;
    private AppCompatButton scanBtn, cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        setContentView(R.layout.activity_capture_img);

        // Initialize the views
        buttonsCardView = findViewById(R.id.buttons_cardview);
        uploadImageContainer = findViewById(R.id.upload_image_container);
        headerContainer = findViewById(R.id.header_container);
        headerTxt = findViewById(R.id.header_txt);

        //Uploaded Image View
        uploadedImg = findViewById(R.id.uploaded_img);

        //Buttons
        scanBtn = findViewById(R.id.scan_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        backBtn = findViewById(R.id.back_btn);
        saveBtn = findViewById(R.id.save_btn);


    }
}