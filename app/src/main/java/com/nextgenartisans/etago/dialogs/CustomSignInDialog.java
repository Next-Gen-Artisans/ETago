package com.nextgenartisans.etago.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import com.nextgenartisans.etago.R;

public class CustomSignInDialog extends Dialog {

    private TextView progressText;
    private ProgressBar progressBar;
    private ImageView checkIcon, xIcon;
    private AppCompatButton proceedButton;

    public CustomSignInDialog(Context context) {
        super(context, R.style.Theme_ETago);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_google_signin_dialog, null);
        progressText = view.findViewById(R.id.signing_in_dialog_title);
        progressBar = view.findViewById(R.id.signingin_progbar);
        checkIcon = view.findViewById(R.id.check_icon);
        xIcon = view.findViewById(R.id.x_icon);
        proceedButton = view.findViewById(R.id.return_login_dialog_btn);
        setContentView(view);

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

    public void setMessage(String message) {
        progressText.setText(message);
    }

    public void showAuthProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        checkIcon.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void showAuthFailedProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        xIcon.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void setProceedButtonClickListener(View.OnClickListener listener) {
        proceedButton.setOnClickListener(listener);
    }

    public void setProceedButtonVisible(boolean visible) {
        proceedButton.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }
}
