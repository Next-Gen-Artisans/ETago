package com.nextgenartisans.etago;

import android.app.Activity;
import android.app.AlertDialog;
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

public class LogoutDialog extends Dialog {
    LinearLayout logoutDialogButtons;
    AppCompatButton logoutDialogBtn, cancelLogoutDialogBtn;

    public LogoutDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_logout_dialog);

        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        logoutDialogButtons = findViewById(R.id.logout_dialog_buttons);
        logoutDialogBtn = findViewById(R.id.logout_dialog_btn);
        cancelLogoutDialogBtn = findViewById(R.id.cancel_logout_dialog_btn);

        logoutDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Sign out from FirebaseAuth
                FirebaseAuth.getInstance().signOut();

                // Create an Intent to start the Welcome activity
                Intent intent = new Intent(getContext(), Welcome.class);

                // Set flags to clear the activity stack and start a new task
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                // Start the Welcome activity
                getContext().startActivity(intent);

                // Dismiss the dialog
                dismiss();

                // If the context is an instance of an Activity, close it
                if (getContext() instanceof Activity) {
                    ((Activity) getContext()).finish();
                }
            }
        });


        cancelLogoutDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the dialog
                dismiss();
            }
        });


    }


}
