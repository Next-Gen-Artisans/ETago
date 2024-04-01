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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.camera.core.ImageCapture;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.api.ETagoAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetectionActivity extends AppCompatActivity {

    private static final int INPUT_IMG_SIZE = 768;
    private static final float CONFIDENCE_THRESHOLD = 0.5f;
    private LinearLayout headerContainer;
    private ImageButton backBtn, saveBtn;
    private TextView headerTxt;
    private CardView buttonsCardView;
    private AppCompatImageView detectedImg;
    private LinearLayout buttonsLayout;
    private AppCompatButton censorBtn, cancelBtn;
    private ImageCapture imageCapture;

    private Uri annotatedimageUri;
    private Uri selectedImageUri;



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
        censorBtn = findViewById(R.id.censor_btn);
        cancelBtn = findViewById(R.id.cancel_btn);

        //View
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                .build();


        // Get the path of the annotated image
        String uriString = getIntent().getStringExtra("annotated_image_uri");
        String selectedImageUriString = getIntent().getStringExtra("selected_image_uri");
        if (uriString != null && !uriString.isEmpty()) {
            annotatedimageUri = Uri.parse(uriString);
            selectedImageUri = Uri.parse(selectedImageUriString);
            try {
                InputStream inputStream = getContentResolver().openInputStream(annotatedimageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                detectedImg.setImageBitmap(bitmap); // Ensure detectedImg is initialized
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Unable to load image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No annotated image received", Toast.LENGTH_SHORT).show();
        }


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetectionActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DetectionActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        censorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetectionActivity.this, "Censoring image...", Toast.LENGTH_SHORT).show();

                byte[] imageData = getImageData(selectedImageUri);

                if (imageData != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/jpeg"), imageData);

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://192.168.1.22:8001/") // Ensure the port is included if necessary
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ETagoAPI api = retrofit.create(ETagoAPI.class);
                    Call<ResponseBody> call = api.uploadImageForCensoring(MultipartBody.Part.createFormData("file", "image.jpg", requestFile));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                                Toast.makeText(DetectionActivity.this, "Image censored successfully", Toast.LENGTH_SHORT).show();
                                //Log the response
                                Log.i("DetectionActivityLog", "Image censored successfully");

                                // Save the bitmap to a temporary file
                                String fileName = "temp_censored_image.jpg"; // Temporary file name
                                File outputFile = new File(getExternalCacheDir(), fileName);
                                try {
                                    FileOutputStream out = new FileOutputStream(outputFile);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.flush();
                                    out.close();

                                    // Pass the path of the temporary file to CensorActivity
                                    Intent intent = new Intent(DetectionActivity.this, CensorActivity.class);
                                    intent.putExtra("censored_image_path", outputFile.getAbsolutePath());
                                    startActivity(intent);

                                } catch (IOException e) {
                                    Log.e("DetectionActivityLog", "Error saving censored image", e);
                                    Toast.makeText(DetectionActivity.this, "Error saving censored image", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                // Handling non-successful response
                                String responseBody = "N/A";
                                try {
                                    responseBody = response.errorBody().string();
                                } catch (Exception e) {
                                    Log.e("DetectionActivityLog", "Error reading response body", e);
                                }
                                Log.e("DetectionActivityLog", "Failed to censor image: " + responseBody);
                                Toast.makeText(DetectionActivity.this, "Failed to censor image", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(DetectionActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("DetectionActivityLog", "Error uploading image: " + t.getMessage());
                        }
                    });
                } else {
                    Toast.makeText(DetectionActivity.this, "Error preparing image for upload", Toast.LENGTH_SHORT).show();
                    Log.e("DetectionActivityLog", "Error preparing image for upload");
                }
            }
        });
    }

    private byte[] getImageData(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }












}