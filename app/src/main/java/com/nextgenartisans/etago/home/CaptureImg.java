package com.nextgenartisans.etago.home;

import static android.content.ContentValues.TAG;

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
import androidx.camera.core.CameraControl;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.api.ETagoAPI;
import com.nextgenartisans.etago.dialogs.CustomSignInDialog;
import com.nextgenartisans.etago.model.CensorshipInstance;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
    private TextView headerTxt;
    private AppCompatImageView capturedImg;
    private AppCompatButton captureBtn, cancelBtn, resetPreviewBtn;
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
    private boolean usingFrontCamera = false;
    private CameraControl cameraControl;

    //Declare Uri variables
    private Uri capturedImgUri, annotatedImageUri, censoredImageUri;

    // Declare the global Intent
    private Intent detectionActivityIntent;

    private StringBuilder objectsDetected;
    private ImageButton flashButton;
    private boolean isFrontCamera = false;

    private FirebaseFirestore db;
    private FirebaseUser firebaseUser;

    //Initialize captured classes
    Map<String, Double> capturedClasses = new HashMap<>();

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
        capturedImg = findViewById(R.id.captured_img);
        frameLayout = findViewById(R.id.container);
        previewView = findViewById(R.id.preview_view);

        //Buttons
        captureBtn = findViewById(R.id.capture_scan_btn);
        resetPreviewBtn = findViewById(R.id.reset_preview_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize FirebaseUser
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize the Intent
        detectionActivityIntent = new Intent(CaptureImg.this, DetectionActivity.class);

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

        resetPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previewView.setVisibility(View.VISIBLE);
                capturedImg.setVisibility(View.GONE);
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

        ImageButton rotateCameraButton = findViewById(R.id.rotate_cam);
        rotateCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usingFrontCamera = !usingFrontCamera; // Toggle camera facing
                cameraProviderFuture.addListener(() -> {
                    try {
                        ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                        bindPreview(cameraProvider); // Rebind camera with new facing
                    } catch (ExecutionException | InterruptedException e) {
                        Toast.makeText(CaptureImg.this, "Error switching camera.", Toast.LENGTH_SHORT).show();
                    }
                }, ContextCompat.getMainExecutor(CaptureImg.this));
            }
        });

        flashButton = findViewById(R.id.flashlight_btn);

        // Set OnClickListener for rotate_cam button
        flashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraControl != null) {
                    // Check if multiple cameras are available
                    if (isFrontCamera) {
                        // Switch to the back camera
                        cameraControl.enableTorch(false);
                        isFrontCamera = false;
                    } else {
                        // Switch to the front camera
                        cameraControl.enableTorch(true);
                        isFrontCamera = true;
                    }
                }
            }
        });

    }

    private void takePicture() {
        File file = new File(getExternalFilesDir(null), "photo.jpg");
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(file).build();

        // Create the dialog
        CustomSignInDialog dialog = new CustomSignInDialog(CaptureImg.this);

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                        Uri capturedImageUri = Uri.fromFile(file); // 'file' is the File object used in takePicture()

                        // Display the snapshot
                        takeSnapshot();

                        byte[] imageData = getImageData(capturedImageUri, 768, 100);
                        if (imageData != null) {
                            // Fetch the current apiCallsLimit
                            DocumentReference docRef = db.collection("Users").document(firebaseUser.getUid());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document != null && document.exists()) {
                                            long currentApiCallsLimit = document.getLong("apiCallsLimit");
                                            if (currentApiCallsLimit > 0) {
                                                // If apiCallsLimit is greater than 0, proceed with the API calls
                                                uploadImage(imageData, capturedImageUri);
                                            } else {
                                                // If apiCallsLimit is 0 or less, show a message to the user
                                                dialog.show();
                                                dialog.setMessage("You have reached your free limit. Please notify the administrator.");
                                                dialog.showAuthFailedProgress(true);
                                                dialog.showAuthFailedProgress(false);
                                                dialog.setProceedButtonVisible(true); // Show the proceed button
                                                dialog.setProceedButtonClickListener(v -> {
                                                    dialog.dismiss(); // Close the dialog
                                                    finish();
                                                });// Set click listener to dismiss the dialog
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "Error getting document", task.getException());
                                    }
                                }
                            });

                        } else {
                            dialog.show();
                            dialog.setMessage("Error preparing image for upload");
                            dialog.showAuthFailedProgress(true);
                            dialog.setProceedButtonVisible(true); // Show the proceed button
                            dialog.setProceedButtonClickListener(v -> {
                                dialog.dismiss(); // Close the dialog
                                previewView.setVisibility(View.VISIBLE);
                                capturedImg.setVisibility(View.GONE); // Reset the preview
                            }); // Set click listener to dismiss the dialog
                        }
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        dialog.show();
                        dialog.setMessage("Image unsaved" + exception.toString());
                        dialog.showAuthFailedProgress(true);
                        dialog.setProceedButtonVisible(true); // Show the proceed button
                        dialog.setProceedButtonClickListener(v -> {
                            dialog.dismiss(); // Close the dialog
                            previewView.setVisibility(View.VISIBLE);
                            capturedImg.setVisibility(View.GONE); // Reset the preview
                        }); // Set click listener to dismiss the dialog
                    }
                });
    }


    // Method to take a snapshot of the preview
    private void takeSnapshot() {
        PreviewView previewView = findViewById(R.id.preview_view);
        Bitmap bitmap = previewView.getBitmap();
        if (bitmap != null) {
            previewView.setVisibility(View.GONE); // Hide the preview
            capturedImg.setImageBitmap(bitmap); // Assume capturedImg is your ImageView to display the snapshot
            capturedImg.setVisibility(View.VISIBLE); // Make the ImageView visible
        }
    }

    private void uploadImage(byte[] imageData, Uri capturedImageUri) {
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageData);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ETagoAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ETagoAPI api = retrofit.create(ETagoAPI.class);
        handleImageUpload(api, requestFile, capturedImageUri);
    }

    private void handleImageUpload(ETagoAPI api, RequestBody requestFile, Uri capturedImageUri) {

        // Create and show the dialog
        CustomSignInDialog dialog = new CustomSignInDialog(CaptureImg.this);
        dialog.setMessage("Uploading image...");
        dialog.showAuthProgress(true);
        dialog.setProceedButtonVisible(false); // Hide the proceed button
        dialog.show();

        objectsDetected = new StringBuilder();
        Call<ResponseBody> call1 = api.uploadImageForJson(MultipartBody.Part.createFormData("file", "image.jpg", requestFile));
        call1.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String jsonResponse = response.body().string();
                        JSONObject jsonObject = new JSONObject(jsonResponse);

                        if (jsonObject.has("detect_objects") && jsonObject.getJSONArray("detect_objects").length() > 0) {
                            JSONArray detectedObjects = jsonObject.getJSONArray("detect_objects");
                            for (int i = 0; i < detectedObjects.length(); i++) {
                                JSONObject object = detectedObjects.getJSONObject(i);
                                capturedClasses.put(object.getString("name"), object.getDouble("confidence"));
                                objectsDetected.append(object.getString("name"))
                                        .append(" (")
                                        .append(String.format("%.2f", object.getDouble("confidence") * 100))
                                        .append("%)\n"); // Use \n for new line
                            }
                            objectsDetected.setLength(objectsDetected.length() - 1); // Remove the last comma
                        } else {
                            objectsDetected.append(jsonObject.optString("message", "No objects detected."));
                        }
                        // Now that JSON processing is done, proceed to annotated image processing
                        dialog.dismiss();
                        processAnnotatedImage(api, requestFile, objectsDetected);

                    } catch (Exception e) {
                        Log.e("CaptureImgLog", "Error parsing detection results: " + e.getMessage());
                        dialog.setMessage("Error processing detection results. Please try again.");
                        dialog.showAuthProgress(false);
                        dialog.showAuthFailedProgress(true);
                        dialog.setProceedButtonVisible(true); // Show the proceed button
                        dialog.setProceedButtonClickListener(v -> {
                            dialog.dismiss(); // Close the dialog
                            previewView.setVisibility(View.VISIBLE);
                            capturedImg.setVisibility(View.GONE); // Reset the preview
                        }); // Set click listener to dismiss the dialog
                    }
                } else {
                    logError("API Call1", response);
                    dialog.setMessage("Failed to upload the image. Server may be down. Please notify the administrator.");
                    dialog.showAuthProgress(false);
                    dialog.showAuthFailedProgress(true);
                    dialog.setProceedButtonVisible(true); // Show the proceed button
                    dialog.setProceedButtonClickListener(v -> {
                        dialog.dismiss(); // Close the dialog
                        previewView.setVisibility(View.VISIBLE);
                        capturedImg.setVisibility(View.GONE); // Reset the preview
                    }); // Set click listener to dismiss the dialog
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                logFailure("API Call1", t);
                dialog.setMessage("Error uploading image. Server may be down. Please notify the administrator.");
                dialog.showAuthFailedProgress(true);
                dialog.showAuthFailedProgress(false);
                dialog.setProceedButtonVisible(true); // Show the proceed button
                dialog.setProceedButtonClickListener(v -> {
                    dialog.dismiss(); // Close the dialog
                    previewView.setVisibility(View.VISIBLE);
                    capturedImg.setVisibility(View.GONE); // Reset the preview
                }); // Set click listener to dismiss the dialog
            }
        });
    }

    private void logError(String tag, Response<ResponseBody> response) {
        Log.e("CaptureImgLog", tag + " Failed: " + response.errorBody().charStream().toString());
    }

    private void logFailure(String tag, Throwable t) {
        Log.e("CaptureImgLog", tag + " Error uploading image: " + t.getMessage());
    }

    private void processAnnotatedImage(ETagoAPI api, RequestBody requestFile, StringBuilder objectsDetected) {
        Call<ResponseBody> call2 = api.uploadImageForAnnotation(MultipartBody.Part.createFormData("file", "image.jpg", requestFile));
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    File annotatedFile = saveBitmapToFile(bitmap);

                    if (annotatedFile != null) {
                        annotatedImageUri = Uri.fromFile(annotatedFile);

                        capturedImg.setImageURI(annotatedImageUri);
                        // Update the global Intent with the annotated image URI
                        //detectionActivityIntent.putExtra("annotated_image_uri", annotatedImageUri);

                        // Proceed to process the censored image
                        processCensoredImage(api, requestFile, objectsDetected);
                    } else {
                        Log.e("CaptureImgLog", "Failed to save annotated image.");
                        Toast.makeText(CaptureImg.this, "Failed to process annotated image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    logError("API Call2", response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                logFailure("API Call2", t);
            }
        });
    }

    private void processCensoredImage(ETagoAPI api, RequestBody requestFile, StringBuilder objectsDetected) {
        Call<ResponseBody> call3 = api.uploadImageForCensoring(MultipartBody.Part.createFormData("file", "image.jpg", requestFile));
        call3.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    File censoredFile = saveBitmapToFile(bitmap);


                    if (censoredFile != null) {
                        censoredImageUri = Uri.fromFile(censoredFile);
                        // Update the global Intent with the censored image URI
                        //detectionActivityIntent.putExtra("censored_image_uri", censoredImageUri);
                        //detectionActivityIntent.putExtra("annotated_image_uri", annotatedImageUri);
                        // Now that both URIs are added to the Intent, show the bottom sheet dialog


                        showBottomSheetDialog(objectsDetected.toString(), annotatedImageUri, censoredImageUri);
                    } else {
                        Log.e("CaptureImgLog", "Failed to save censored image.");
                        Toast.makeText(CaptureImg.this, "Failed to process censored image.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    logError("API Call3", response);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                logFailure("API Call3", t);
            }
        });
    }

    // This method is supposed to handle the response logic and any UI updates based on that
    private void processUploadResponse(Response<ResponseBody> response, StringBuilder objectsDetected, Uri capturedImageUri) {
        //Toast to tell the user that this method is called
        Toast.makeText(CaptureImg.this, "Response received", Toast.LENGTH_SHORT).show();

    }

    private void decrementApiCallsLimit(FirebaseUser firebaseUser) {
        DocumentReference docRef = db.collection("Users").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        long currentApiCallsLimit = document.getLong("apiCallsLimit");
                        if (currentApiCallsLimit > 0) {
                            docRef.update("apiCallsLimit", currentApiCallsLimit - 1);
                        } else {
                            Log.d(TAG, "API calls limit reached.");
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting document", task.getException());
                }
            }
        });
    }

    private void showBottomSheetDialog(String detectedText, Uri annotatedImageUri, Uri censoredImageUri) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_detected_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CaptureImg.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Update text views for detected objects
        TextView textView = bottomSheetView.findViewById(R.id.btm_dialog_text);
        textView.setText(detectedText);

        bottomSheetDialog.show();

        AppCompatButton cancelButton = bottomSheetView.findViewById(R.id.btm_cancel_dialog_btn);
        AppCompatButton proceedButton = bottomSheetView.findViewById(R.id.btm_proceed_dialog_btn);

        cancelButton.setOnClickListener(v -> {
            // Dismiss the bottom sheet dialog
            bottomSheetDialog.dismiss();

            // Set the preview view to visible
            previewView.setVisibility(View.VISIBLE);

            // Set the captured image view to gone
            capturedImg.setVisibility(View.GONE);
        });
        proceedButton.setOnClickListener(v -> {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            DocumentReference docRef = db.collection("Users").document(user.getUid());
            docRef.update("numCensoredImgs", FieldValue.increment(1))
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));

            // Generate a unique ID for this censorship instance
            String censorshipID = UUID.randomUUID().toString();
            // Create a new CensorshipInstance object
            CensorshipInstance censorshipInstance = new CensorshipInstance();
            censorshipInstance.setCensorshipID(censorshipID);
            censorshipInstance.setUserID(firebaseUser.getUid());
            censorshipInstance.setCapturedClasses(capturedClasses);
            censorshipInstance.setDateCensored(FieldValue.serverTimestamp());

            // Save the CensorshipInstance object to Firestore
            db.collection("CensorshipInstances").add(censorshipInstance)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d("CaptureImg", "Censorship instance recorded with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("CaptureImg", "Error recording censorship instance", e);
                        }
                    });

            // Decrement the API calls limit
            decrementApiCallsLimit(firebaseUser);
            detectionActivityIntent.putExtra("censored_image_uri", censoredImageUri.toString());
            detectionActivityIntent.putExtra("annotated_image_uri", annotatedImageUri.toString());
            bottomSheetDialog.cancel();
            startActivity(detectionActivityIntent);
        });
    }

    private File saveBitmapToFile(Bitmap bitmap) {
        // Create a file in the external cache directory
        File outputFile = new File(getExternalCacheDir(), "annotated_image.jpg");
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            // Compress the bitmap and write to the output file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
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
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos); // Compress to JPEG and adjust quality
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(isFrontCamera ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
        cameraControl = camera.getCameraControl();

        // Set OnClickListener for flashButton
        flashButton.setOnClickListener(v -> {
            if (cameraControl != null) {
                // Check if multiple cameras are available
                if (isFrontCamera) {
                    // Switch to the back camera
                    cameraControl.enableTorch(false);
                    isFrontCamera = false;
                } else {
                    // Switch to the front camera
                    cameraControl.enableTorch(true);
                    isFrontCamera = true;
                }
            }
        });

        // Set OnClickListener for rotateCameraButton
        ImageButton rotateCameraButton = findViewById(R.id.rotate_cam);
        rotateCameraButton.setOnClickListener(v -> {
            isFrontCamera = !isFrontCamera; // Toggle camera facing
            cameraProvider.unbindAll(); // Unbind all use cases before rebinding
            bindPreview(cameraProvider); // Rebind camera with new facing
        });
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


    }

    // Method to request permissions
    public void requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS);
    }

    // Replace this with your own method to start the camera
    private void startCamera() {
        previewView.setVisibility(View.VISIBLE);
        capturedImg.setVisibility(View.GONE);
        Toast.makeText(this, "Camera started", Toast.LENGTH_SHORT).show();
    }

}