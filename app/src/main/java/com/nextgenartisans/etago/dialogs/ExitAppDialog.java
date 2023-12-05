package com.nextgenartisans.etago.dialogs;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.nextgenartisans.etago.R;

public class ExitAppDialog extends Dialog {
    LinearLayout exitDialogButtons;
    AppCompatButton exitDialogBtn, cancelExitDialogBtn;

    public ExitAppDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_exit_dialog);

        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        exitDialogButtons = findViewById(R.id.exit_dialog_buttons);
        exitDialogBtn = findViewById(R.id.exit_dialog_btn);
        cancelExitDialogBtn = findViewById(R.id.cancel_exit_dialog_btn);

        Log.d(TAG, "ExitAppDialog created");

        exitDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Exit button clicked");
                // Call a method that finishes the activity
                exitApp();
            }
        });

        cancelExitDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Cancel button clicked");
                // Simply dismiss the dialog
                dismiss();
            }
        });
    }

    private void exitApp() {
        // Exit the application
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }


}
