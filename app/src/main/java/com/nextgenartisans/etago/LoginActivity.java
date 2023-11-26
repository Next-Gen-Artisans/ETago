package com.nextgenartisans.etago;

import static android.content.ContentValues.TAG;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    LinearLayout loginHeader, loginFooter, loginOptions;
    CardView loginForm;
    ImageButton loginBackBtn, facebookLoginBtn, googleLoginBtn;
    TextInputLayout loginEmailInput, loginPassInput;
    TextInputEditText userEmail, userPass;
    TextView forgotPass, textSignUp;
    AppCompatButton loginBtn;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }
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
        googleLoginBtn = findViewById(R.id.facebook_login_btn);
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
        mAuth = FirebaseAuth.getInstance();

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

        // TODO LOGIN BACKEND
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


                // Before your Firebase Authentication code:
                ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
                progressDialogFragment.show(getSupportFragmentManager(), "progress_dialog");

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // Dismiss the progress dialog when Firebase task is complete
                                progressDialogFragment.dismiss();

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
        facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Feature coming soon!", Toast.LENGTH_SHORT).show();
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




}