package com.nextgenartisans.etago;

import android.app.Activity;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;

public class TermsOfServiceDialog extends Dialog {
    LinearLayout termsPrivacyLayout;
    TextView termsText, privacyText;
    AppCompatButton agreeTermsDialogBtn;


    public TermsOfServiceDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_terms_of_service_dialog);

        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        termsPrivacyLayout = findViewById(R.id.terms_privacy_layout);
        termsText = findViewById(R.id.terms_text);
        privacyText = findViewById(R.id.privacy_text);
        agreeTermsDialogBtn = findViewById(R.id.agree_terms_dialog_btn);

        agreeTermsDialogBtn.setOnClickListener(new View.OnClickListener() {
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

    }


}
