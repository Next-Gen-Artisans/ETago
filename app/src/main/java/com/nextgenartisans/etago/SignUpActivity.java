package com.nextgenartisans.etago;

import static android.content.ContentValues.TAG;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {


    LinearLayout signupHeader, signupFooter, signupOptions;
    ImageButton signupBackBtn, facebookSignupBtn, googleSignupBtn;
    CardView signupForm;
    TextInputLayout signupUsernameInput, signupEmailInput, signupPassInput;
    TextInputEditText userSignupUsername, userSignupEmail, userSignupPass;
    AppCompatButton signUpBtn;
    TextView textLogIn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    private CustomSignInDialog customSignInDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        //Set Content View
        setContentView(R.layout.activity_sign_up);

        //Set id for clickable text and buttons
        signUpBtn = findViewById(R.id.signup_btn);
        textLogIn = findViewById(R.id.text_log_in);
        facebookSignupBtn = findViewById(R.id.facebook_signup_btn);
        googleSignupBtn = findViewById(R.id.google_signup_btn);
        signupBackBtn = findViewById(R.id.signup_back_btn);

        //Set id for non-interactive components
        signupHeader = findViewById(R.id.signup_header);
        signupFooter = findViewById(R.id.signup_footer);
        signupOptions = findViewById(R.id.signup_options);
        signupForm = findViewById(R.id.signup_form);


        //FIREBASE INSTANCE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Set id for input text
        signupUsernameInput = findViewById(R.id.signup_username_input);
        signupEmailInput = findViewById(R.id.signup_email_input);
        signupPassInput = findViewById(R.id.signup_pass_input);
        userSignupUsername = findViewById(R.id.user_signup_username);
        userSignupEmail = findViewById(R.id.user_signup_email);
        userSignupPass = findViewById(R.id.user_signup_pass);


        //Remove animation when input text is focused by user
        userSignupUsername.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                signupUsernameInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (userSignupUsername.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    signupUsernameInput.setHint("Username");
                }
            }
        });

        userSignupEmail.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                signupEmailInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (userSignupEmail.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    signupEmailInput.setHint("Email");
                }
            }
        });

        userSignupPass.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                signupPassInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (userSignupPass.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    signupPassInput.setHint("Password");
                }
            }
        });


        textLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        signupBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, Welcome.class);
                startActivity(i);
                finish();
            }
        });

        // TODO SIGN UP BACKEND
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username, email, password;

                username = String.valueOf(userSignupUsername);
                email = userSignupEmail.getText().toString();
                password = userSignupPass.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Before your Firebase Authentication code:
                ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
                progressDialogFragment.show(getSupportFragmentManager(), "progress_dialog");

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Dismiss the progress dialog when Firebase task is complete
                                progressDialogFragment.dismiss();

                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();


                                    Toast.makeText(SignUpActivity.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();

                                    FirebaseAuth var = FirebaseAuth.getInstance();
                                    var.signOut();

                                    Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });

        // Initialize the custom progress dialog
        customSignInDialog = new CustomSignInDialog(this);

        // Setting the listener for the proceed button in the dialog
        customSignInDialog.setProceedButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customSignInDialog.dismiss(); // Dismiss the dialog
                updateUI(); // Call updateUI when the proceed button is clicked

            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // TODO FB AND GOOGLE SIGN UP BACKEND
        facebookSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        googleSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

    }

    int RC_SIGN_IN = 40;

    private void googleSignIn() {
        mGoogleSignInClient.signOut();
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Show the custom dialog with progress
                customSignInDialog.setMessage("Creating account...");
                customSignInDialog.showAuthProgress(true);
                customSignInDialog.setProceedButtonVisible(false);
                customSignInDialog.show();

                firebaseAuth(account.getIdToken());

            } catch (ApiException e) {
                // Handle error
                // Update dialog to show error message
                customSignInDialog.setMessage("Creating account failed.");
                customSignInDialog.showAuthFailedProgress(false);
            }


        }

    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Check if user already exists in Firestore
                                checkUserInFirestore(firebaseUser);

                            } else {
                                // Authentication failed
                                customSignInDialog.setMessage("Authentication failed.");
                                customSignInDialog.showAuthFailedProgress(false);
                            }
                        } else {
                            // Handle the sign in error (e.g., display a message)
                        }
                    }
                });
    }

    private void checkUserInFirestore(FirebaseUser firebaseUser) {
        DocumentReference docRef = db.collection("Users").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // User already exists in Firestore, just log in
                        Log.d(TAG, "User already exists in Firestore.");

                        // Redirect to main activity or update UI
                        // Authentication success
                        customSignInDialog.setMessage("Account already exists. Logging in.");
                        customSignInDialog.showAuthProgress(false); // Show check icon
                        customSignInDialog.setProceedButtonVisible(true); // Show proceed button


                    } else {
                        // User does not exist, create a new user document
                        createUserInFirestore(firebaseUser);
                    }
                } else {
                    // Handle errors here
                }
            }
        });
    }

    private void createUserInFirestore(FirebaseUser firebaseUser) {
        Users users = new Users();
        users.setUserID(firebaseUser.getUid());
        users.setUsername(firebaseUser.getDisplayName());
        users.setEmail(firebaseUser.getEmail());
        users.setProfilePic(firebaseUser.getPhotoUrl().toString());

        db.collection("Users").document(firebaseUser.getUid())
                .set(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Successfully added new user
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        // Redirect to main activity or update UI
                        // Authentication success
                        customSignInDialog.setMessage("Account created.");
                        customSignInDialog.showAuthProgress(false); // Show check icon
                        customSignInDialog.setProceedButtonVisible(true); // Show proceed button
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void updateUI() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    private void promptLogin(){
        mGoogleSignInClient.signOut();
        //mAuth.signOut();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }


}