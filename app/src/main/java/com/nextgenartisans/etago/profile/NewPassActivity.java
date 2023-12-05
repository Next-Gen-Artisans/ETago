package com.nextgenartisans.etago.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nextgenartisans.etago.MainActivity;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.dialogs.CustomSignInDialog;

public class NewPassActivity extends AppCompatActivity {

    // Declare the views
    private CardView editPassHeader;
    private LinearLayout editPassContainers, editPassHeaderContainer;
    private ImageButton editPassBackBtn;
    private TextView editPassHeaderText, forgotPass;
    private TextInputLayout editPassInputLayout1, editPassInputLayout2, editPassInputLayout3;
    private TextInputEditText editPassPassInput, editPassNewInput, editPassConfirmInput;
    private AppCompatButton updatePassBtn, skipNewPassBtn;
    CustomSignInDialog customSignInDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_new_pass);

        // Initialize the views
        editPassHeader = findViewById(R.id.edit_pass_header);
        editPassContainers = findViewById(R.id.edit_pass_containers);
        editPassHeaderContainer = findViewById(R.id.edit_pass_header_container);
        editPassHeaderText = findViewById(R.id.edit_pass_header_txt);

        //Buttons
        editPassBackBtn = findViewById(R.id.edit_pass_back_btn);
        updatePassBtn = findViewById(R.id.update_pass_btn);

        //Edit Texts Layouts
        editPassInputLayout2 = findViewById(R.id.edit_pass_input_2);
        editPassInputLayout3 = findViewById(R.id.edit_pass_input_3);

        //Edit Texts
        editPassNewInput = findViewById(R.id.edit_pass_new_input);
        editPassConfirmInput = findViewById(R.id.edit_pass_confirm_input);

        customSignInDialog = new CustomSignInDialog(NewPassActivity.this);

        //Remove animation when input text is focused by user
        editPassPassInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editPassInputLayout1.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editPassPassInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editPassInputLayout1.setHint("Password");
                }
            }
        });

        //Remove animation when input text is focused by user
        editPassNewInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editPassInputLayout2.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editPassNewInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editPassInputLayout2.setHint("New Password");
                }
            }
        });

        //Remove animation when input text is focused by user
        editPassConfirmInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editPassInputLayout3.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editPassConfirmInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editPassInputLayout3.setHint("Confirm Password");
                }
            }
        });

        //BACK BUTTON TO PROFILE ACTIVITY
        editPassBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NewPassActivity.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });

        updatePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = editPassNewInput.getText().toString();
                String confirmNewPassword = editPassConfirmInput.getText().toString();

                if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                    Toast.makeText(NewPassActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmNewPassword)) {
                    Toast.makeText(NewPassActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                customSignInDialog.setMessage("Updating Password...");
                customSignInDialog.showAuthProgress(true);
                customSignInDialog.show();

                // Update the password
                updatePassword(newPassword);
            }
        });

        //TODO
        // Skip button logic
        skipNewPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirect to MainActivity without setting a password
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    customSignInDialog.setMessage("Password set successfully.");
                    customSignInDialog.showAuthProgress(false);
                    // Redirect to MainActivity
                    Intent intent = new Intent(NewPassActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Handle failure
                    customSignInDialog.setMessage("Failed to set password: " + task.getException().getMessage());
                    customSignInDialog.showAuthFailedProgress(true);
                }
                customSignInDialog.setProceedButtonVisible(true);
                customSignInDialog.setProceedButtonClickListener(v -> customSignInDialog.dismiss());
            });
        }
    }


}