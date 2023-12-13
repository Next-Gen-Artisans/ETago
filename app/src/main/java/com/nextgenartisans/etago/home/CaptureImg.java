package com.nextgenartisans.etago.home;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
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
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

public class CaptureImg extends AppCompatActivity {

    // Declare member variables for each view
    private CardView buttonsCardView;
    private LinearLayout headerContainer;
    private ImageButton backBtn, saveBtn;
    private TextView headerTxt;
    private AppCompatImageView capturedImg;
    private AppCompatButton captureBtn, cancelBtn;
    private FrameLayout frameLayout;
    private PreviewView previewView;

    private ActivityResultLauncher<String[]> activityResultLauncher;

    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final String[] CAMERA_PERMISSION = new String[]{Manifest.permission.CAMERA};

    private static final int CAMERA_REQUEST_CODE = 10;

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
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

        setContentView(R.layout.activity_capture_img);

        // Initialize the views
        buttonsCardView = findViewById(R.id.buttons_cardview);

        headerContainer = findViewById(R.id.header_container);
        headerTxt = findViewById(R.id.header_txt);

        //Captured Image View
        //capturedImg = findViewById(R.id.captured_img);
        frameLayout = findViewById(R.id.container);
        previewView = findViewById(R.id.preview_view);

        //Buttons
        captureBtn = findViewById(R.id.capture_scan_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        backBtn = findViewById(R.id.back_btn);
        saveBtn = findViewById(R.id.save_btn);

        //View
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();

        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean permissionGranted = true;
                    for (Map.Entry<String, Boolean> entry : permissions.entrySet()) {
                        if (Arrays.asList(REQUIRED_PERMISSIONS).contains(entry.getKey()) && !entry.getValue()) {
                            permissionGranted = false;
                            break;
                        }
                    }
                    if (!permissionGranted) {
                        Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT).show();
                    } else {
                        startCamera(); // Replace with your method to start the camera
                    }
                }
        );

        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));


    }

    private void takePicture() {
        File file = new File(getExternalFilesDir(null), "photo.jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

//                        // Create a file in the Pictures directory
//                        File photoDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//                        File photo = new File(photoDir, "photo_" + System.currentTimeMillis() + ".jpg");
//
//                        try {
//                            // Copy the captured image to the new file
//                            try (InputStream in = new FileInputStream(outputFileResults.getSavedUri().getPath());
//                                 OutputStream out = new FileOutputStream(photo)) {
//                                byte[] buf = new byte[1024];
//                                int len;
//                                while ((len = in.read(buf)) > 0) {
//                                    out.write(buf, 0, len);
//                                }
//                            }
//                            // Notify the gallery
//                            addPicToGallery(photo.getAbsolutePath());
//                            Toast.makeText(CaptureImg.this, "Image saved to gallery", Toast.LENGTH_SHORT).show();
//                        } catch (IOException e) {
//                            Log.e("CaptureImg", "Error saving image to gallery", e);
//                            Toast.makeText(CaptureImg.this, "Error saving image", Toast.LENGTH_SHORT).show();
//                        }

                        Toast.makeText(CaptureImg.this, "Image sent to Detection Activity", Toast.LENGTH_SHORT).show();

                        // Get the saved image file URI
                        Uri savedUri = outputFileResults.getSavedUri();
                        if (savedUri == null) {
                            savedUri = Uri.fromFile(file);
                        }

                        // Create an intent to start DetectionActivity
                        Intent intent = new Intent(CaptureImg.this, DetectionActivity.class);
                        // Pass the image file path as an extra
                        intent.putExtra("image_path", savedUri.toString());
                        startActivity(intent);
                        finish();


                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CaptureImg.this, "Image unsaved" + exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPicToGallery(String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (hasCameraPermission()) {
            startCamera();
        } else {
            requestPermission();
        }

        //requestPermissions();
    }

    // Method to request permissions
    public void requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS);
    }

    // Replace this with your own method to start the camera
    private void startCamera() {
        Toast.makeText(this, "Camera started", Toast.LENGTH_SHORT).show();
    }

}