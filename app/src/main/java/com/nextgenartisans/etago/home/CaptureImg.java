package com.nextgenartisans.etago.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.api.ETagoAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
                        Toast.makeText(CaptureImg.this, "Image captured, starting upload...", Toast.LENGTH_SHORT).show();
                        Uri capturedImageUri = Uri.fromFile(file); // 'file' is the File object used in takePicture()

                        byte[] imageData = getImageData(capturedImageUri, 768, 80);
                        if (imageData != null) {
                            RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);

                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(ETagoAPI.BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            ETagoAPI api = retrofit.create(ETagoAPI.class);
                            Call<ResponseBody> call = api.uploadImageForAnnotation(MultipartBody.Part.createFormData("file", "image.jpg", requestFile));

                            call.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(CaptureImg.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                                        // Decode the received bitmap from the response
                                        Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                                        // Save the bitmap to a temporary file
                                        File outputFile = saveBitmapToFile(bitmap); // This method is already defined

                                        if (outputFile != null) {
                                            Uri annotatedImageUri = Uri.fromFile(outputFile);

                                            // Pass the URI of the temporary file to DetectionActivity
                                            Intent intent = new Intent(CaptureImg.this, DetectionActivity.class);
                                            intent.putExtra("selected_image_uri", capturedImageUri.toString());
                                            intent.putExtra("annotated_image_uri", annotatedImageUri.toString());
                                            startActivity(intent);
                                        } else {
                                            Log.e("CaptureImgLog", "Failed to save the annotated image");
                                            Toast.makeText(CaptureImg.this, "Failed to process the image.", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(CaptureImg.this, "Upload failed", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(CaptureImg.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(CaptureImg.this, "Error preparing image for upload", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CaptureImg.this, "Image unsaved" + exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        // Create a file in the external cache directory
        File outputFile = new File(getExternalCacheDir(), "annotated_image.jpg");
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            // Compress the bitmap and write to the output file
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            // Return the file
            return outputFile;
        } catch (IOException e) {
            Log.e("UploadImg", "Error saving bitmap to file", e);
            return null;
        }
    }

    private byte[] getImageData(Uri imageUri, int targetWidth, int quality) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            // Optional: Resize the image to maintain aspect ratio but fit within a certain width if desired
            if (targetWidth > 0) {
                int originalWidth = bitmap.getWidth();
                int originalHeight = bitmap.getHeight();
                float aspectRatio = (float) originalWidth / (float) originalHeight;
                int targetHeight = Math.round(targetWidth / aspectRatio);
                bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos); // Compress to JPEG and adjust quality
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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