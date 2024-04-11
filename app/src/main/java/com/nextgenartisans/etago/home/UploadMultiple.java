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
import com.nextgenartisans.etago.api.ETagoAPI;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadMultiple extends AppCompatActivity implements ImagesAdapter.OnItemClickListener {
    private RecyclerView imagesRecyclerView;
    private ImagesAdapter imagesAdapter;
    private ArrayList<Uri> selectedImages = new ArrayList<>();
    private ArrayList<Bitmap> processedImages = new ArrayList<>();
    private List<Call<ResponseBody>> ongoingCalls = new ArrayList<>();

    private AppCompatButton scanPhotosBtn, resetSelectionBtn, cancelBtn;
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

    // Declare Retrofit and API service
    private Retrofit retrofit;
    private ETagoAPI apiService;

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


        setContentView(R.layout.activity_upload_multiple_img);

        // Initialize the views
        scanPhotosBtn = findViewById(R.id.scan_photos_btn);
        resetSelectionBtn = findViewById(R.id.reset_selection_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        backBtn = findViewById(R.id.back_btn);

        imagesRecyclerView = findViewById(R.id.uploaded_imgs_rv);
        imagesAdapter = new ImagesAdapter(this, selectedImages, this);
        imagesRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        imagesRecyclerView.setAdapter(imagesAdapter);

        // Immediately launch the photo picker when the activity starts
        pickMultipleMedia.launch("image/*");

        // Initialize Retrofit and API service
        initializeRetrofit();

        //Handle click events show toast messages first
        scanPhotosBtn.setOnClickListener(v -> {
            // Show a toast message
            Toast.makeText(this, "Scan photos button clicked", Toast.LENGTH_SHORT).show();

            if (!selectedImages.isEmpty()) {
                uploadImages(selectedImages);
            } else {
                Toast.makeText(UploadMultiple.this, "No images selected for upload.", Toast.LENGTH_SHORT).show();
            }

        });

        resetSelectionBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Reset selection button clicked", Toast.LENGTH_SHORT).show();

            // Cancel all ongoing calls
            for (Call<ResponseBody> call : ongoingCalls) {
                call.cancel();
            }
            ongoingCalls.clear();

            // Clear the selected images list and notify the adapter
            selectedImages.clear();
            imagesAdapter.notifyDataSetChanged();

            // Relaunch the media picker to select images again
            pickMultipleMedia.launch("image/*");
        });

        cancelBtn.setOnClickListener(v -> {
            // Show a toast message
            Toast.makeText(this, "Cancel button clicked", Toast.LENGTH_SHORT).show();

            // Close the activity and go back to MainActivity
            Intent intent = new Intent(UploadMultiple.this, MainActivity.class);
            startActivity(intent);
            finish();

        });

        backBtn.setOnClickListener(v -> {
            // Show a toast message
            Toast.makeText(this, "Back button clicked", Toast.LENGTH_SHORT).show();

            finish();
        });

    }

    private void initializeRetrofit() {
        retrofit = new Retrofit.Builder()
                .baseUrl(ETagoAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ETagoAPI.class);
    }

    private void uploadImages(ArrayList<Uri> uris) {

        for (int i = 0; i < uris.size(); i++) {
            int index = i;
            Uri uri = uris.get(index);
            byte[] imageData = getImageData(uri, 768, 100);
            if (imageData != null) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), imageData);
                MultipartBody.Part body = MultipartBody.Part.createFormData("file", "image.jpg", requestFile);

                Call<ResponseBody> call = apiService.uploadImageForAnnotation(body);
                ongoingCalls.add(call);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!call.isCanceled() && response.isSuccessful()) {
                            Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                            File annotatedFile = saveBitmapToFile(bitmap);
                            if (annotatedFile != null) {
                                Uri annotatedImageUri = Uri.fromFile(annotatedFile);
                                processedImages.set(index, bitmap);
                                selectedImages.set(index, annotatedImageUri);
                                imagesAdapter.notifyItemChanged(index);
                            }
                        } else if (!call.isCanceled()) {
                            Toast.makeText(UploadMultiple.this, "Failed to upload and process image.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        if (!call.isCanceled()) {
                            Toast.makeText(UploadMultiple.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
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
            Log.e("UploadMultImg", "Error saving bitmap to file", e);
            return null;
        }
    }

    private byte[] getImageData(Uri imageUri, int targetWidth, int quality) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            // Optionally resize the image
            if (targetWidth > 0) {
                int originalWidth = bitmap.getWidth();
                int originalHeight = bitmap.getHeight();
                float aspectRatio = (float) originalWidth / (float) originalHeight;
                int targetHeight = Math.round(targetWidth / aspectRatio);
                bitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, false);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            Log.e("UploadMultiple", "Error preparing image for upload: " + e.getMessage());
            return null;
        }
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
