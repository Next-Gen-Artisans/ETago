package com.nextgenartisans.etago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    LinearLayout loginHeader, loginFooter, loginOptions;
    CardView loginForm;
    ImageButton loginBackBtn, facebookLoginBtn, googleLoginBtn;
    TextInputLayout loginEmailInput, loginPassInput;
    TextInputEditText userEmail, userPass;
    TextView forgotPass, textSignUp;
    AppCompatButton loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Make app full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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




        //Remove animation when input text is focused by user
        userEmail.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                loginEmailInput.setHint("");
            } else {
                // When the input field is deselected (loses focus), restore the hint text
                loginEmailInput.setHint("Email");
            }
        });

        userPass.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                loginPassInput.setHint("");
            } else {
                // When the input field is deselected (loses focus), restore the hint text
                loginPassInput.setHint("Password");
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
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        // TODO FB AND GOOGLE LOGIN BACKEND
        facebookLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });










    }
}