package com.nextgenartisans.etago.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.nextgenartisans.etago.R;

public class OnMultipleImgClickDialog extends Dialog {

    private ImageView imageView;
    private TextView detectedObjectsText, title;
    private AppCompatButton closeDialogBtn;


    public OnMultipleImgClickDialog(@NonNull Context context) {
        super(context, R.style.Theme_ETago);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        initDialog(context);
    }

    private void initDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_onmultimg_dialog, null);
        setContentView(view);

        //Declare views
        imageView = view.findViewById(R.id.on_mult_detected_objects_img);
        detectedObjectsText = view.findViewById(R.id.on_mult_detected_objects_txt);
        title = view.findViewById(R.id.onmult_dialog_title);
        closeDialogBtn = view.findViewById(R.id.close_dialog_btn);

        // Set up the close button
        closeDialogBtn.setOnClickListener(v -> dismiss());

        setupWindow();

    }

    private void setupWindow() {
        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());

            // Set the size of the dialog
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            // Set the position of the dialog
            layoutParams.gravity = Gravity.CENTER;

            // Set the amount of dimming for the background
            layoutParams.dimAmount = 0.5f; // you can adjust the value as per your need

            // Apply the updated layout parameters to the dialog window
            window.setAttributes(layoutParams);

            // This line is required to apply the dimming
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
    }

    public void setImage(Uri imageUri) {
        ImageView imageView = findViewById(R.id.on_mult_detected_objects_img);
        Glide.with(getContext()).load(imageUri).into(imageView);

        // Optionally, you can update other elements such as title or detected objects text
        TextView title = findViewById(R.id.onmult_dialog_title);
        title.setText("View Image");  // Example title

        // Handle detected objects text dynamically if necessary
        TextView detectedObjectsText = findViewById(R.id.on_mult_detected_objects_txt);
        detectedObjectsText.setText("Objects Detected: Example Object 1, Object 2");
    }
}
