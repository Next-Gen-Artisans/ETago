package com.nextgenartisans.etago;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.Manifest;


import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MediaPermissionDialog extends Dialog {

    LinearLayout mediaPermissionDialogButtons;
    AppCompatButton mediaPermissionDialogBtn, cancelMediaPermissionDialogBtn;
    TextView mediaPermissionDialogTitle, mediaPermissionDialogText;


    private int storagePermissionCode;

    public MediaPermissionDialog(@NonNull Context context, int storagePermissionCode) {
        super(context);
        this.storagePermissionCode = storagePermissionCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_media_permission_dialog);

        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        mediaPermissionDialogTitle = findViewById(R.id.media_permission_dialog_title);
        mediaPermissionDialogText = findViewById(R.id.media_permission_dialog_text);
        mediaPermissionDialogButtons = findViewById(R.id.media_permission_dialog_buttons);
        mediaPermissionDialogBtn = findViewById(R.id.media_permission_dialog_btn);
        cancelMediaPermissionDialogBtn = findViewById(R.id.cancel_media_permission_dialog_btn);

        mediaPermissionDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User clicked to grant permission
                // Request the actual permission from MainActivity
                if (getContext() instanceof MainActivity) {
                    ActivityCompat.requestPermissions((MainActivity) getContext(),
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MainActivity.STORAGE_PERMISSION_CODE);
                }
                dismiss();
            }
        });

        cancelMediaPermissionDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User cancelled the dialog
                dismiss();
            }
        });

    }

}
