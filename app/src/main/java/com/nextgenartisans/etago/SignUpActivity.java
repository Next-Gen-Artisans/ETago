package com.nextgenartisans.etago;

import static android.content.ContentValues.TAG;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
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
        mAuth = FirebaseAuth.getInstance();

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

                if (TextUtils.isEmpty(username)){
                    Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)){
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
                Toast.makeText(getApplicationContext(), "Feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void firebaseAuth(String idToken) {
        try {
            if (idToken != null) {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);
                mAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("id",user.getUid());
                                    map.put("username",user.getDisplayName());
                                    map.put("email",user.getEmail().toString());


                                    // Add a new document with a generated ID
                                    db.collection("users")
                                            .add(user)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                }
                                            });

                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithCredential:success");
                                    updateUI();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private void updateUI() {



    }


}