package com.nextgenartisans.etago.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.dialogs.CustomSignInDialog;

public class NewPassActivity extends AppCompatActivity {

    // Declare the views
    private CardView editPassHeader;
    private LinearLayout editPassContainers, editPassHeaderContainer;
    private ImageButton editPassBackBtn;
    private TextView forgotPass;
    private TextInputLayout editPassInputLayout1, editPassInputLayout2, editPassInputLayout3;
    private TextInputEditText editPassPassInput, editPassNewInput, editPassConfirmInput;
    private AppCompatButton updatePassBtn, cancelPassBtn;
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

        //Buttons
        editPassBackBtn = findViewById(R.id.edit_pass_back_btn);
        updatePassBtn = findViewById(R.id.update_pass_btn);
        cancelPassBtn = findViewById(R.id.cancel_new_pass_btn);

        //Edit Texts Layouts
        editPassInputLayout2 = findViewById(R.id.edit_pass_input_2);
        editPassInputLayout3 = findViewById(R.id.edit_pass_input_3);

        //Edit Texts
        editPassNewInput = findViewById(R.id.edit_pass_new_input);
        editPassConfirmInput = findViewById(R.id.edit_pass_confirm_input);

        customSignInDialog = new CustomSignInDialog(NewPassActivity.this);


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

        cancelPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

                // Proceed with Firebase password update
                updatePassword(newPassword);
            }
        });



    }

    private void reauthenticateUser(String email, String currentPassword, final String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // User re-authenticated successfully, now update the password
                        updatePassword(newPassword);
                    } else {
                        // Re-authentication failed
                        customSignInDialog.setMessage("Re-authentication failed: " + task.getException().getMessage());
                        customSignInDialog.showAuthFailedProgress(true);
                    }
                }
            });
        }
    }




    private void updatePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Directly updating the password without re-authentication
            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        // Password update successful
                        customSignInDialog.setMessage("Password update successful.");
                        customSignInDialog.showAuthProgress(false);

                        // Update the 'isPasswordSet' field in Firestore for the current user
                        updateIsPasswordSetFlag();
                    } else {
                        // Handle failure
                        customSignInDialog.setMessage("Failed to update password: " + task.getException().getMessage());
                        customSignInDialog.showAuthFailedProgress(true);
                    }

                    // Setting up the proceed button
                    customSignInDialog.setProceedButtonVisible(true);
                    customSignInDialog.setProceedButtonClickListener(v -> {
                        customSignInDialog.dismiss();
                        finish();
                    });
                }
            });
        }
    }

    private void updateIsPasswordSetFlag() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Set the 'isPasswordSet' field to true
            db.collection("Users").document(userId)
                    .update("userPasswordSet", true)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "isPasswordSet successfully updated to true"))
                    .addOnFailureListener(e -> Log.d("Firestore", "Error updating isPasswordSet", e));
        }
    }



}