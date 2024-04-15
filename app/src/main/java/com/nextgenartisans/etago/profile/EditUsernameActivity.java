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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class EditUsernameActivity extends AppCompatActivity {

    // Declare the views
    private CardView editUsernameHeader;
    private LinearLayout editUsernameContainers, editUsernameHeaderContainer;
    private ImageButton editUsernameBackBtn;
    private TextView editUsernameHeaderText;
    private TextInputLayout editUsernameInputLayout1, editUsernameInputLayout2, editUsernameInputLayout3;
    private TextInputEditText editUsernamePassInput, editUsernameNewInput, editUsernameConfirmInput;
    private AppCompatButton updateUsernameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_edit_username);

        // Initialize the views
        editUsernameHeader = findViewById(R.id.edit_username_header);
        editUsernameContainers = findViewById(R.id.edit_username_containers);
        editUsernameHeaderContainer = findViewById(R.id.edit_username_header_container);
        editUsernameHeaderText = findViewById(R.id.edit_username_header_txt);

        //Buttons
        editUsernameBackBtn = findViewById(R.id.edit_username_back_btn);
        updateUsernameBtn = findViewById(R.id.update_username_btn);

        //Edit Texts Layouts
        editUsernameInputLayout1 = findViewById(R.id.edit_username_input_1);
        editUsernameInputLayout2 = findViewById(R.id.edit_username_input_2);
        editUsernameInputLayout3 = findViewById(R.id.edit_username_input_3);

        //Edit Texts
        editUsernamePassInput = findViewById(R.id.edit_username_pass_input);
        editUsernameNewInput = findViewById(R.id.edit_username_new_input);
        editUsernameConfirmInput = findViewById(R.id.edit_username_confirm_input);



        //Remove animation when input text is focused by user
        editUsernamePassInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editUsernameInputLayout1.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editUsernamePassInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editUsernameInputLayout1.setHint("Password");
                }
            }
        });

        //Remove animation when input text is focused by user
        editUsernameNewInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editUsernameInputLayout2.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editUsernameNewInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editUsernameInputLayout2.setHint("New Username");
                }
            }
        });

        //Remove animation when input text is focused by user
        editUsernameConfirmInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editUsernameInputLayout3.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editUsernameConfirmInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editUsernameInputLayout3.setHint("Confirm Username");
                }
            }
        });

        //BACK BUTTON TO PROFILE ACTIVITY
        editUsernameBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        updateUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = editUsernamePassInput.getText().toString();
                String newUsername = editUsernameNewInput.getText().toString();
                String confirmUsername = editUsernameConfirmInput.getText().toString();

                // Initialize the custom dialog
                CustomSignInDialog customSignInDialog = new CustomSignInDialog(EditUsernameActivity.this);

                // Check if all fields are filled
                if (currentPassword.isEmpty() || newUsername.isEmpty() || confirmUsername.isEmpty()) {
                    Toast.makeText(EditUsernameActivity.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if new username and confirm username match
                if (!newUsername.equals(confirmUsername)) {
                    Toast.makeText(EditUsernameActivity.this, "Usernames do not match.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Display the dialog indicating the authentication process is starting
                customSignInDialog.setMessage("Authenticating...");
                customSignInDialog.showAuthProgress(true);
                customSignInDialog.show();

                // Authenticate with Firebase
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), currentPassword);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Update username in Firestore
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("Users").document(user.getUid())
                                        .update("username", newUsername)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                customSignInDialog.setMessage("Username updated successfully.");
                                                customSignInDialog.showAuthProgress(false);


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                customSignInDialog.setMessage("Failed to update username.");
                                                customSignInDialog.showAuthFailedProgress(true);

                                            }
                                        });
                            } else {
                                customSignInDialog.setMessage("Authentication failed.");
                                customSignInDialog.showAuthFailedProgress(true);
                            }
                            customSignInDialog.setProceedButtonVisible(true);
                            customSignInDialog.setProceedButtonClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    customSignInDialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    });
                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();



    }
}