package com.nextgenartisans.etago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignUpActivity extends AppCompatActivity {


    LinearLayout signupHeader, signupFooter, signupOptions;
    ImageButton signupBackBtn, facebookSignupBtn, googleSignupBtn;
    CardView signupForm;
    TextInputLayout signupUsernameInput, signupEmailInput, signupPassInput;
    TextInputEditText userSignupUsername, userSignupEmail, userSignupPass;
    AppCompatButton signUpBtn;
    TextView textLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Make app full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
                // When the input field is deselected (loses focus), restore the hint text
                signupUsernameInput.setHint("Username");
            }
        });

        userSignupEmail.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                signupEmailInput.setHint("");
            } else {
                // When the input field is deselected (loses focus), restore the hint text
                signupEmailInput.setHint("Email");
            }
        });

        userSignupPass.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                signupPassInput.setHint("");
            } else {
                // When the input field is deselected (loses focus), restore the hint text
                signupPassInput.setHint("Password");
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
                Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        // TODO FB AND GOOGLE SIGN UP BACKEND
        facebookSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        googleSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });







    }
}