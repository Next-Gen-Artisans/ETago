package com.nextgenartisans.etago.home;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.camera.core.ImageCapture;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.nextgenartisans.etago.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CensorActivity extends AppCompatActivity {

    // Declare views
    private LinearLayout headerContainer;
    private ImageButton backButton;
    private TextView headerText;
    private CardView buttonsCardView;
    private AppCompatImageView censoredImage;
    private LinearLayout buttonsLayout;
    private AppCompatButton saveButton, cancelButton;

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

        setContentView(R.layout.activity_censor);


        // Initialize views
        headerContainer = findViewById(R.id.header_container);
        backButton = findViewById(R.id.back_btn);
        headerText = findViewById(R.id.header_txt);
        buttonsCardView = findViewById(R.id.buttons_cardview);
        censoredImage = findViewById(R.id.censored_img);
        buttonsLayout = findViewById(R.id.buttons_layout);
        saveButton = findViewById(R.id.save_btn);
        cancelButton = findViewById(R.id.cancel_btn);

        //View
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        // Get the image path from the intent
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("censored_image_path");
        if (imagePath != null && !imagePath.isEmpty()) {
            // Load the image from the path
            Uri imageUri = Uri.parse(imagePath);
            censoredImage.setImageURI(imageUri);
        }

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CensorActivity.this, DetectionActivity.class);
                startActivity(i);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CensorActivity.this, DetectionActivity.class);
                startActivity(i);
                finish();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePicture();
            }
        });

    }

    private void savePicture() {
        // Get the image path from the intent
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("censored_image_path");
        if (imagePath == null || imagePath.isEmpty()) {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show();
            return;
        }


        File sourceFile = new File(imagePath);
        if (!sourceFile.exists()) {
            Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show();
            return;
        }

        File photoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "YourAppName");
        if (!photoDir.exists() && !photoDir.mkdirs()) {
            Log.e("CensorActivity", "Failed to create directory");
            Toast.makeText(this, "Failed to create directory in gallery", Toast.LENGTH_SHORT).show();
            return;
        }

        File photo = new File(photoDir, "censored_photo_" + System.currentTimeMillis() + ".jpg");

        try {
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(photo);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            // Notify the gallery
            addPicToGallery(photo.getAbsolutePath());
            Toast.makeText(CensorActivity.this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("CensorActivity", "Error saving image to gallery", e);
            Toast.makeText(CensorActivity.this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPicToGallery(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}