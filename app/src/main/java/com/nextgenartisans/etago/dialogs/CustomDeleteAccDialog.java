package com.nextgenartisans.etago.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.onboarding.Welcome;

public class CustomDeleteAccDialog extends Dialog {

    private ProgressBar progressBar;
    private ImageView checkIcon, xIcon;

    private TextInputLayout deleteAccPass, deleteAccConfirmPass;
    private TextInputEditText deleteAccPassInput, deleteAccConfirmPassInput;
    private TextView progressText, subtitleText;
    private LinearLayout formContainer, buttonsContainer, successContainer;
    private AppCompatButton proceedButton, cancelButton, successButton;

    public CustomDeleteAccDialog(Context context) {
        super(context, R.style.Theme_ETago);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_delete_acc_dialog, null);
        setContentView(view);

        progressBar = view.findViewById(R.id.signingin_progbar);
        checkIcon = view.findViewById(R.id.check_icon);
        xIcon = view.findViewById(R.id.x_icon);
        deleteAccPass = view.findViewById(R.id.delete_acc_pass);
        deleteAccConfirmPass = view.findViewById(R.id.delete_acc_confirm_pass);
        deleteAccPassInput = view.findViewById(R.id.delete_acc_pass_input);
        deleteAccConfirmPassInput = view.findViewById(R.id.delete_acc_confirm_pass_input);
        progressText = view.findViewById(R.id.delete_acc_dialog_title);
        subtitleText = view.findViewById(R.id.delete_acc_dialog_subtitle);
        formContainer = view.findViewById(R.id.form_container);
        buttonsContainer = view.findViewById(R.id.delete_acc_dialog_buttons);
        successContainer = view.findViewById(R.id.delete_acc_success);

        //Remove animation when input text is focused by user
        deleteAccPassInput.setOnFocusChangeListener((view1, hasFocus) -> {
            if (hasFocus) {
                deleteAccPass.setHint("");
            } else {
                if (deleteAccPassInput.getText().toString().isEmpty()) {
                    deleteAccPass.setHint("Password");
                }
            }
        });

        deleteAccConfirmPassInput.setOnFocusChangeListener((view1, hasFocus) -> {
            if (hasFocus) {
                deleteAccConfirmPass.setHint("");
            } else {
                if (deleteAccConfirmPassInput.getText().toString().isEmpty()) {
                    deleteAccConfirmPass.setHint("Confirm Password");
                }
            }
        });

        proceedButton = view.findViewById(R.id.delete_acc_proceed_dialog_btn);
        cancelButton = view.findViewById(R.id.delete_acc_cancel_dialog_btn);
        successButton = view.findViewById(R.id.delete_acc_success_btn);

        proceedButton.setOnClickListener(v -> {
            // Hide the keyboard
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentFocus = getCurrentFocus();
            if (currentFocus != null) {
                imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
            }

            // Attempt to delete the account
            hideKeyboard();
            attemptDelete(context);
        });

        cancelButton.setOnClickListener(v -> {
            resetDialog();
            dismiss();
        });
        successButton.setOnClickListener(v -> navigateToLogin(context));

        setupWindow();
    }

    private void resetDialog() {
        progressBar.setVisibility(View.GONE);
        checkIcon.setVisibility(View.GONE);
        xIcon.setVisibility(View.GONE);
        progressText.setText("Delete Account");
        subtitleText.setText("Please confirm your password below to delete your account.");
        subtitleText.setVisibility(View.VISIBLE);

        // Reset text fields
        deleteAccPassInput.setText("");
        deleteAccConfirmPassInput.setText("");

        // Reset the visibility of containers
        formContainer.setVisibility(View.VISIBLE);
        buttonsContainer.setVisibility(View.VISIBLE);
        successContainer.setVisibility(View.GONE);
    }



    private void setupWindow() {
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

    private void attemptDelete(Context context) {
        String password = deleteAccPassInput.getText().toString().trim();
        String confirmPassword = deleteAccConfirmPassInput.getText().toString().trim();

        if (TextUtils.isEmpty(password) || !password.equals(confirmPassword)) {
            Toast.makeText(context, "Complete all fields. Passwords must match.", Toast.LENGTH_LONG).show();
            return;
        }

        xIcon.setVisibility(View.GONE);
        showProgress(true);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnSuccessListener(aVoid -> {
                // Get a Firestore instance
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // Get a reference to the user's document in Firestore
                DocumentReference userRef = db.collection("Users").document(user.getUid());
                userRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String profilePicUrl = documentSnapshot.getString("profilePic");
                        if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                            // Create a reference to the file in Firebase Storage
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference profilePicRef = storage.getReferenceFromUrl(profilePicUrl);

                            // Delete the file from Firebase Storage
                            profilePicRef.delete().addOnSuccessListener(aVoid1 -> {
                                // File deleted successfully, now delete the user's document
                                userRef.delete().addOnSuccessListener(aVoid2 -> {
                                    // Now, delete the user's account from Firebase Authentication
                                    user.delete().addOnSuccessListener(aVoid3 -> {
                                        updateDialogForSuccess();
                                    }).addOnFailureListener(e -> {
                                        updateDialogForFailure("Failed to delete account: " + e.getMessage());
                                    });
                                }).addOnFailureListener(e -> {
                                    updateDialogForFailure("Failed to delete user data from Firestore: " + e.getMessage());
                                });
                            }).addOnFailureListener(e -> {
                                updateDialogForFailure("Failed to delete profile picture from Firebase Storage: " + e.getMessage());
                            });
                        }
                    }
                }).addOnFailureListener(e -> {
                    updateDialogForFailure("Failed to get user data from Firestore: " + e.getMessage());
                });
            }).addOnFailureListener(e -> {
                updateDialogForFailure("Reauthentication failed: Incorrect Password");
            });
        } else {
            Toast.makeText(context, "No user is signed in.", Toast.LENGTH_SHORT).show();
        }
    }


    private void showProgress(boolean show) {
        //Set margin below progressBar
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) progressBar.getLayoutParams();
        params.setMargins(0, 0, 0, show ? 32 : 0);
        progressBar.setLayoutParams(params);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        formContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        buttonsContainer.setVisibility(View.GONE);
        progressText.setText(show ? "Deleting Account..." : "Delete Account");
        subtitleText.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void updateDialogForSuccess() {
        progressBar.setVisibility(View.GONE);
        subtitleText.setVisibility(View.GONE);
        checkIcon.setVisibility(View.VISIBLE);
        progressText.setText("Account deleted successfully.");
        successContainer.setVisibility(View.VISIBLE);
    }

    private void updateDialogForFailure(String message) {
        progressBar.setVisibility(View.GONE);
        xIcon.setVisibility(View.VISIBLE);
        subtitleText.setVisibility(View.GONE);
        progressText.setText(message);
        formContainer.setVisibility(View.GONE);
        buttonsContainer.setVisibility(View.VISIBLE);
        proceedButton.setText("Try Again"); // Change button text to "Try Again"
        proceedButton.setOnClickListener(v -> resetAndRetry(getContext())); // Set click listener for retry
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View currentFocus = getCurrentFocus();
        if (currentFocus != null) {
            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



    private void resetAndRetry(Context context) {
          // Hide the keyboard before resetting the dialog
        resetDialog(); // Reset all fields and visibility states
        proceedButton.setText("Proceed"); // Change back the button text to "Proceed"
        proceedButton.setOnClickListener(v -> {
            hideKeyboard();  // Also hide the keyboard when retry is attempted
            attemptDelete(context);
        });
        subtitleText.setVisibility(View.VISIBLE); // Make sure to show the subtitle text again
    }

    private void navigateToLogin(Context context) {
        Intent intent = new Intent(context, Welcome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        dismiss();
    }
}
