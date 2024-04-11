package com.nextgenartisans.etago.home;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.adapter.ImagesAdapter;

import java.util.ArrayList;

public class DetectMultiple extends AppCompatActivity implements ImagesAdapter.OnItemClickListener {
    private RecyclerView imagesRecyclerView;
    private ImagesAdapter imagesAdapter;
    private ArrayList<Uri> selectedImages = new ArrayList<>();

    private AppCompatButton savePhotosBtn, cancelBtn;
    private ImageButton backBtn;

    // Registers a photo picker activity launcher in multi-select mode.
    ActivityResultLauncher<String> pickMultipleMedia = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(),
            uris -> {
                // Callback is invoked after the user selects media items or closes the photo picker.
                if (!uris.isEmpty()) {
                    selectedImages.clear();
                    selectedImages.addAll(uris);
                    imagesAdapter.notifyDataSetChanged();
                }
            }
    );

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


        setContentView(R.layout.activity_detect_multiple_img);

        // Initialize the views
        savePhotosBtn = findViewById(R.id.save_multiple_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        backBtn = findViewById(R.id.back_btn);

        imagesRecyclerView = findViewById(R.id.detected_imgs_rv);
        imagesAdapter = new ImagesAdapter(this, selectedImages, this);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imagesRecyclerView.setAdapter(imagesAdapter);

        // Immediately launch the photo picker when the activity starts
        pickMultipleMedia.launch("image/*");

        // Assuming you have logic to handle the intent and extract the URI or data passed
        String annotatedImageUri = getIntent().getStringExtra("annotatedUri");
        updateRecyclerViewWithAnnotatedImage(annotatedImageUri);

        //Handle click events show toast messages first
        savePhotosBtn.setOnClickListener(v -> {
            // Show a toast message
            Toast.makeText(this, "Save photos button clicked", Toast.LENGTH_SHORT).show();

        });

        cancelBtn.setOnClickListener(v -> {
            // Show a toast message
            Toast.makeText(this, "Cancel button clicked", Toast.LENGTH_SHORT).show();

            // Close the activity and go back to MainActivity
            Intent intent = new Intent(DetectMultiple.this, MainActivity.class);
            startActivity(intent);
            finish();

        });

        backBtn.setOnClickListener(v -> {
            // Show a toast message
            Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show();

            finish();
        });

    }

    private void updateRecyclerViewWithAnnotatedImage(String imageUri) {
        // Update your RecyclerView with this new image data
        selectedImages.add(Uri.parse(imageUri));
        imagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Uri uri) {
        // Handle the click event here
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }





}
