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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

public class ForgotPassDialog extends Dialog {

    LinearLayout passResetDialogButtons;
    AppCompatButton returnLoginBtn;

    public ForgotPassDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_forgot_pass_dialog);

        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        passResetDialogButtons = findViewById(R.id.pass_reset_dialog_buttons);
        returnLoginBtn = findViewById(R.id.return_login_dialog_btn);


        returnLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the dialog
                dismiss();

                // Redirect to LoginActivity
                Context context = getContext();
                context.startActivity(new Intent(context, LoginActivity.class));
                // If you want to finish the current activity as well, you might need to use a callback or a broadcast receiver

            }
        });
    }
}
