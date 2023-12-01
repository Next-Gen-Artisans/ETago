package com.nextgenartisans.etago;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
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
import com.google.firebase.auth.GoogleAuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    LinearLayout loginHeader, loginFooter, loginOptions;
    CardView loginForm;
    ImageButton loginBackBtn, facebookLoginBtn, googleLoginBtn;
    TextInputLayout loginEmailInput, loginPassInput;
    TextInputEditText userEmail, userPass;
    TextView forgotPass, textSignUp;
    AppCompatButton loginBtn;

    //Firebase Authentication and Firestore
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    private CustomSignInDialog customSignInDialog;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI();
        }
    }

    private void updateUI() {
        finish();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

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
        setContentView(R.layout.activity_login);


        //Set id for clickable text and buttons
        loginBtn = findViewById(R.id.login_btn);
        loginBackBtn = findViewById(R.id.login_back_btn);
        facebookLoginBtn = findViewById(R.id.facebook_login_btn);
        googleLoginBtn = findViewById(R.id.google_login_btn);
        forgotPass = findViewById(R.id.forgot_pass);
        textSignUp = findViewById(R.id.text_sign_up);

        //Set id for non-interactive components
        loginHeader = findViewById(R.id.login_header);
        loginFooter = findViewById(R.id.login_footer);
        loginOptions = findViewById(R.id.login_options);
        loginForm = findViewById(R.id.login_form);

        //Set id for input text
        loginEmailInput = findViewById(R.id.login_email_input);
        loginPassInput = findViewById(R.id.login_pass_input);
        userEmail = findViewById(R.id.user_email);
        userPass = findViewById(R.id.user_pass);

        //FIREBASE INSTANCE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Remove animation when input text is focused by user
        userEmail.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                loginEmailInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (userEmail.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    loginEmailInput.setHint("Email");
                }
            }
        });

        userPass.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                loginPassInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (userPass.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    loginPassInput.setHint("Password");
                }
            }
        });

        textSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        loginBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, Welcome.class);
                startActivity(i);
                finish();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;

                email = userEmail.getText().toString();
                password = userPass.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(getApplicationContext(), "Login Successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(i);
                                    finish();

                                    FirebaseUser user = mAuth.getCurrentUser();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });


            }

        });


        // TODO FB AND GOOGLE LOGIN BACKEND

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


        facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, ForgotPass.class);
                startActivity(i);
                finish();
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
                customSignInDialog.setMessage("Authenticating...");
                customSignInDialog.showAuthProgress(true);
                customSignInDialog.setProceedButtonVisible(false);
                customSignInDialog.show();


                firebaseAuth(account.getIdToken());

            } catch (ApiException e) {
                // Handle error
                // Update dialog to show error message
                customSignInDialog.setMessage("Authentication failed.");
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
                        customSignInDialog.setMessage("Account authenticated.");
                        customSignInDialog.showAuthProgress(false); // Show check icon
                        customSignInDialog.setProceedButtonVisible(true); // Show proceed button


                    } else {
                        customSignInDialog.setMessage("Account does not exist.");
                        customSignInDialog.showAuthFailedProgress(false); // Show check icon
                        //customSignInDialog.setProceedButtonVisible(true); // Show proceed button

                        // User does not exist, prompt to sign up
                        promptSignUp();

                        //createUserInFirestore(firebaseUser);
                    }
                } else {
                    customSignInDialog.setMessage("Account does not exist.");
                    customSignInDialog.showAuthFailedProgress(false); // Show check icon
                    //customSignInDialog.setProceedButtonVisible(true); // Show proceed button

                    // User does not exist, prompt to sign up
                    promptSignUp();
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
                        customSignInDialog.setMessage("Account authenticated.");
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

    private void promptSignUp() {
        // Prompt the user to sign up
        Toast.makeText(LoginActivity.this, "No existing account found. Please sign up first.", Toast.LENGTH_LONG).show();
        customSignInDialog.dismiss();
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }


}