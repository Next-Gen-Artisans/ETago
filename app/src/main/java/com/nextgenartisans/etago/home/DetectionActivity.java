package com.nextgenartisans.etago.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.adapter.ECommerceAdapter;
import com.nextgenartisans.etago.model.ECommercePlatform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DetectionActivity extends AppCompatActivity {


    private LinearLayout headerContainer;
    private ImageButton backBtn;
    private TextView headerTxt;
    private CardView buttonsCardView;
    private AppCompatImageView detectedImg;
    private LinearLayout buttonsLayout;
    private AppCompatButton cancelBtn, saveBtn;
    private ImageCapture imageCapture;

    private Uri censoredImageUri;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detection);
        initializeUI();

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
        cancelBtn = findViewById(R.id.cancel_btn);

        //View
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        censoredImageUri = Uri.parse(getIntent().getStringExtra("censored_image_uri"));
        loadCensoredImage();


        backBtn.setOnClickListener(v -> finish());

        cancelBtn.setOnClickListener(v -> {
            startActivity(new Intent(DetectionActivity.this, MainActivity.class));
            finish();
        });

        saveBtn.setOnClickListener(v -> savePicture());


    }

    private void savePicture() {
        if (censoredImageUri == null) {
            Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show();
            return;
        }

        // Use ContentResolver to get InputStream from Uri
        try (InputStream inputStream = getContentResolver().openInputStream(censoredImageUri)) {
            if (inputStream == null) {
                Toast.makeText(this, "Unable to open image", Toast.LENGTH_SHORT).show();
                return;
            }

            File photoDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "ETago");
            if (!photoDir.exists() && !photoDir.mkdirs()) {
                Log.e("DetectionActivity", "Failed to create directory");
                Toast.makeText(this, "Failed to create directory in gallery", Toast.LENGTH_SHORT).show();
                return;
            }

            File photo = new File(photoDir, "censored_photo_" + System.currentTimeMillis() + ".jpg");

            // Copy the stream data to the new file
            try (OutputStream out = new FileOutputStream(photo)) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = inputStream.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }

                // Notify the gallery
                addPicToGallery(photo.getAbsolutePath());
                Toast.makeText(DetectionActivity.this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
                showECommercePlatformsBottomSheet();

            }
        } catch (FileNotFoundException e) {
            Log.e("DetectionActivity", "File not found", e);
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e("DetectionActivity", "Error saving image", e);
            Toast.makeText(this, "Error saving image", Toast.LENGTH_SHORT).show();
        }
    }

    private void showECommercePlatformsBottomSheet() {
        // Inflate the layout for the bottom sheet
        View bottomSheetShareView = getLayoutInflater().inflate(R.layout.bottom_sheet_share_layout, null);
        BottomSheetDialog bottomSheetShareDialog = new BottomSheetDialog(DetectionActivity.this);
        bottomSheetShareDialog.setContentView(bottomSheetShareView);

        // Initialize the list of e-commerce platforms
        List<ECommercePlatform> platforms = new ArrayList<>();
        platforms.add(new ECommercePlatform("Shopee", R.drawable.shopee, "com.shopee.ph"));
        platforms.add(new ECommercePlatform("Lazada", R.drawable.lazada, "com.lazada.android"));
        // Add more platforms with their package names as needed

        // Correctly find the RecyclerView within the bottom sheet view
        RecyclerView recyclerView = bottomSheetShareView.findViewById(R.id.bottom_sheet_share_rv);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            ECommerceAdapter adapter = new ECommerceAdapter(DetectionActivity.this, platforms);
            recyclerView.setAdapter(adapter);
        } else {
            Log.e("RecyclerViewInit", "RecyclerView not found!");
        }

        // Show the bottom sheet dialog
        bottomSheetShareDialog.show();

        AppCompatButton cancelButton = bottomSheetShareView.findViewById(R.id.btm_cancel_dialog_btn);
        cancelButton.setOnClickListener(v -> {
            bottomSheetShareDialog.dismiss();
            Intent intent = new Intent(DetectionActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // This flag will cause any existing task that would be associated with the activity to be cleared before the activity is started.
            startActivity(intent);
            finish(); // Finish the current activity
        });

    }


    private void addPicToGallery(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void loadCensoredImage() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(censoredImageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            detectedImg.setImageBitmap(bitmap);  // Set the censored image to the imageView
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Unable to load image", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


}