package com.nextgenartisans.etago.dialogs;

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

import com.nextgenartisans.etago.MainActivity;
import com.nextgenartisans.etago.R;

public class ForgotPassDialogEditPass extends Dialog {

    LinearLayout passResetDialogButtons;
    AppCompatButton returMainBtn;

    public ForgotPassDialogEditPass(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_forgot_pass_dialog_edit_pass);

        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        passResetDialogButtons = findViewById(R.id.pass_reset_dialog_buttons);
        returMainBtn = findViewById(R.id.return_main_dialog_btn);


        returMainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the dialog
                dismiss();

                // Redirect to LoginActivity
                Context context = getContext();
                context.startActivity(new Intent(context, MainActivity.class));
                // If you want to finish the current activity as well, you might need to use a callback or a broadcast receiver

            }
        });
    }
}
