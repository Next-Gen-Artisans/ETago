package com.nextgenartisans.etago.profile;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.dialogs.CustomSignInDialog;

public class EditEmailActivity extends AppCompatActivity {

    // Declare the views
    private CardView editEmailHeader;
    private LinearLayout editEmailContainers, editEmailHeaderContainer;
    private ImageButton editEmailBackBtn;
    private TextView editEmailHeaderText;
    private TextInputLayout editEmailInputLayout1, editEmailInputLayout2, editEmailInputLayout3;
    private TextInputEditText editEmailPassInput, editEmailNewInput, editEmailConfirmInput;
    private AppCompatButton updateEmailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_edit_email);

        // Initialize the views
        editEmailHeader = findViewById(R.id.edit_email_header);
        editEmailContainers = findViewById(R.id.edit_email_containers);
        editEmailHeaderContainer = findViewById(R.id.edit_email_header_container);
        editEmailHeaderText = findViewById(R.id.edit_email_header_txt);

        //Buttons
        editEmailBackBtn = findViewById(R.id.edit_email_back_btn);
        updateEmailBtn = findViewById(R.id.update_email_btn);

        //Edit Texts Layouts
        editEmailInputLayout1 = findViewById(R.id.edit_email_input_1);
        editEmailInputLayout2 = findViewById(R.id.edit_email_input_2);
        editEmailInputLayout3 = findViewById(R.id.edit_email_input_3);

        //Edit Texts
        editEmailPassInput = findViewById(R.id.edit_email_pass_input);
        editEmailNewInput = findViewById(R.id.edit_email_new_input);
        editEmailConfirmInput = findViewById(R.id.edit_email_confirm_input);


        //Remove animation when input text is focused by user
        editEmailPassInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editEmailInputLayout1.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editEmailPassInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editEmailInputLayout1.setHint("Password");
                }
            }
        });

        //Remove animation when input text is focused by user
        editEmailNewInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editEmailInputLayout2.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editEmailNewInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editEmailInputLayout2.setHint("New Email");
                }
            }
        });

        //Remove animation when input text is focused by user
        editEmailConfirmInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editEmailInputLayout3.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editEmailConfirmInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editEmailInputLayout3.setHint("Confirm Email");
                }
            }
        });

        //BACK BUTTON TO PROFILE ACTIVITY
        editEmailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateEmailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEmailBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String currentPassword = editEmailPassInput.getText().toString();
                        String newEmail = editEmailNewInput.getText().toString();
                        String confirmEmail = editEmailConfirmInput.getText().toString();

                        CustomSignInDialog customSignInDialog = new CustomSignInDialog(EditEmailActivity.this);

                        if (currentPassword.isEmpty() || newEmail.isEmpty() || confirmEmail.isEmpty()) {
                            Toast.makeText(EditEmailActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!newEmail.equals(confirmEmail)) {
                            Toast.makeText(EditEmailActivity.this, "Emails do not match.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        customSignInDialog.setMessage("Updating Email...");
                        customSignInDialog.showAuthProgress(true);
                        customSignInDialog.show();

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                            user.reauthenticate(credential).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    user.verifyBeforeUpdateEmail(newEmail).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("Users").document(user.getUid())
                                                    .update("email", newEmail)
                                                    .addOnSuccessListener(aVoid -> {
                                                        customSignInDialog.setMessage("Verification email sent. Check your new email to confirm the change.");
                                                        customSignInDialog.showAuthProgress(false);
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        customSignInDialog.setMessage("Failed to update email in Firestore.");
                                                        customSignInDialog.showAuthFailedProgress(true);
                                                    });
                                        } else {
                                            customSignInDialog.setMessage("Failed to send verification email.");
                                            customSignInDialog.showAuthFailedProgress(true);
                                        }
                                        customSignInDialog.setProceedButtonVisible(true);
                                        customSignInDialog.setProceedButtonClickListener(v -> customSignInDialog.dismiss());
                                    });
                                } else {
                                    customSignInDialog.setMessage("Authentication failed.");
                                    customSignInDialog.showAuthFailedProgress(true);
                                }
                                customSignInDialog.setProceedButtonVisible(true);
                                customSignInDialog.setProceedButtonClickListener(v -> {
                                    customSignInDialog.dismiss();
                                    finish();
                                });
                            });
                        }
                    }
                });
            }
        });



    }
}