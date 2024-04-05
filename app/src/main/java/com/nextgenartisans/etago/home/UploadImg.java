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

import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.api.ETagoAPI;

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
    private ImageButton backBtn, saveBtn;
    private TextView headerTxt;

    private AppCompatButton scanBtn, cancelBtn;


    //Photo Picker
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private AppCompatImageView uploadedImg;
    private Uri selectedImageUri;


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
        backBtn = findViewById(R.id.back_btn);
        saveBtn = findViewById(R.id.save_btn);


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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        //TODO ML DETECTION AND CENSORSHIP
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(UploadImg.this, "Scanning image...", Toast.LENGTH_SHORT).show();

                if (selectedImageUri != null) {
                    byte[] imageData = getImageData(selectedImageUri, 768, 80);

                    if (imageData != null) {
                        RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(ETagoAPI.BASE_URL) // Replace with your base URL
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        ETagoAPI api = retrofit.create(ETagoAPI.class); // Replace with your API interface
                        Call<ResponseBody> call = api.uploadImageForAnnotation(MultipartBody.Part.createFormData("file", "image.jpg", requestFile));

                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    // Decode the received bitmap from the response
                                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                                    // Save the bitmap to a temporary file
                                    File outputFile = saveBitmapToFile(bitmap);

                                    if (outputFile != null) {
                                        Uri annotatedImageUri = Uri.fromFile(outputFile);

                                        // Pass the URI of the temporary file to DetectionActivity
                                        Intent intent = new Intent(UploadImg.this, DetectionActivity.class);
                                        intent.putExtra("selected_image_uri", selectedImageUri.toString());
                                        intent.putExtra("annotated_image_uri", annotatedImageUri.toString());
                                        startActivity(intent);
                                    } else {
                                        Log.e("UploadImgLog", "Scanning failed: " + response.errorBody().charStream().toString());
                                        Toast.makeText(UploadImg.this, "Failed to scan the image.", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    // Handle non-successful response...
                                    Log.e("UploadImgLog", "Scanning failed: " + response.errorBody().charStream().toString());
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.e("UploadImgLog", "Error uploading image: " + t.getMessage());
                                Toast.makeText(UploadImg.this, "Error uploading image.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(UploadImg.this, "Error preparing image for upload", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UploadImg.this, "No image selected", Toast.LENGTH_SHORT).show();
                }

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
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos); // Compress to JPEG and adjust quality
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
