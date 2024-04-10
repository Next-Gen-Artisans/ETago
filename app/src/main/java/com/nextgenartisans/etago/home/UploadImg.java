package com.nextgenartisans.etago.home;

import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.api.ETagoAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadImg extends AppCompatActivity {

    // Declare member variables for each view
    private CardView buttonsCardView;
    private LinearLayout uploadImageContainer, headerContainer;
    private ImageButton multiInputBtn;
    private TextView headerTxt;

    private AppCompatButton scanBtn, cancelBtn;


    //Photo Picker
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private AppCompatImageView uploadedImg;

    //Declare Uri variables
    private Uri selectedImageUri, annotatedImageUri, censoredImageUri;

    // Declare the global Intent
    private Intent detectionActivityIntent;


    @Override
    protected void onStart() {
        super.onStart();

        // Launch the photo picker and let the user choose only images.
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());

        setImage(selectedImageUri);
    }

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

        setContentView(R.layout.activity_upload_img);

        initializePhotoPicker(); // Initialize the photo picker here

        // Initialize the views
        buttonsCardView = findViewById(R.id.buttons_cardview);
        headerContainer = findViewById(R.id.header_container);
        headerTxt = findViewById(R.id.header_txt);

        //Uploaded Image View
        uploadedImg = findViewById(R.id.uploaded_img);

        //Buttons
        scanBtn = findViewById(R.id.scan_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        multiInputBtn = findViewById(R.id.multi_input_btn);

        // Initialize the Intent
        detectionActivityIntent = new Intent(UploadImg.this, DetectionActivity.class);

        //Click image to replace
        uploadedImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Initialize the photo picker launcher
                launchPhotoPicker();
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



        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UploadImg.this, "Scanning image...", Toast.LENGTH_SHORT).show();

                if (selectedImageUri != null) {
                    byte[] imageData = getImageData(selectedImageUri, 768, 100);

                    if (imageData != null) {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageData);
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ETagoAPI.BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        ETagoAPI api = retrofit.create(ETagoAPI.class);
                        StringBuilder objectsDetected = new StringBuilder();

                        // First API Call - Object Detection JSON
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
                                        processAnnotatedImage(api, requestFile, objectsDetected);

                                    } catch (Exception e) {
                                        Log.e("UploadImgLog", "Error parsing detection results: " + e.getMessage());
                                        Toast.makeText(UploadImg.this, "Failed to parse detection results.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    logError("API Call1", response);
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                logFailure("API Call1", t);
                            }
                        });
                    } else {
                        Toast.makeText(UploadImg.this, "Error preparing image for upload", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UploadImg.this, "No image selected", Toast.LENGTH_SHORT).show();
                }
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

                                uploadedImg.setImageURI(annotatedImageUri);
                                // Update the global Intent with the annotated image URI
                                //detectionActivityIntent.putExtra("annotated_image_uri", annotatedImageUri);

                                // Proceed to process the censored image
                                processCensoredImage(api, requestFile, objectsDetected);
                            } else {
                                Log.e("UploadImgLog", "Failed to save annotated image.");
                                Toast.makeText(UploadImg.this, "Failed to process annotated image.", Toast.LENGTH_SHORT).show();
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
                                Log.e("UploadImgLog", "Failed to save censored image.");
                                Toast.makeText(UploadImg.this, "Failed to process censored image.", Toast.LENGTH_SHORT).show();
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

            private void logError(String tag, Response<ResponseBody> response) {
                Log.e("UploadImgLog", tag + " Failed: " + response.errorBody().charStream().toString());
                Toast.makeText(UploadImg.this, tag + " Failed to scan the image.", Toast.LENGTH_SHORT).show();
            }

            private void logFailure(String tag, Throwable t) {
                Log.e("UploadImgLog", tag + " Error uploading image: " + t.getMessage());
                Toast.makeText(UploadImg.this, tag + " Error uploading image.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private File saveBitmapToFile(Bitmap bitmap, String filename) {
        File outputFile = new File(getExternalCacheDir(), filename);
        try (FileOutputStream out = new FileOutputStream(outputFile)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return outputFile;
        } catch (IOException e) {
            Log.e("UploadImg", "Error saving bitmap to file", e);
            return null;
        }
    }

    private void showBottomSheetDialog(String detectedText, Uri annotatedImageUri, Uri censoredImageUri) {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_detected_layout, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(UploadImg.this);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Update text views for detected objects
        TextView textView = bottomSheetView.findViewById(R.id.btm_dialog_text);
        textView.setText(detectedText);

        bottomSheetDialog.show();

        AppCompatButton cancelButton = bottomSheetView.findViewById(R.id.btm_cancel_dialog_btn);
        AppCompatButton proceedButton = bottomSheetView.findViewById(R.id.btm_proceed_dialog_btn);

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        proceedButton.setOnClickListener(v -> {
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

    private void setImage(Uri uri) {
        selectedImageUri = uri; // Save Uri reference
        uploadedImg.setImageURI(uri);
    }


    private void initializePhotoPicker() {
        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d("PhotoPicker", "Selected URI: " + uri);
                uploadedImg.setImageURI(uri);
                selectedImageUri = uri;

                try {
                    // Persist access permissions.
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (SecurityException e) {
                    Log.e("UploadImg", "Error persisting permission: ", e);
                }
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

    }

    private void launchPhotoPicker() {
        // Launch the photo picker and let the user choose only images
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
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



}
