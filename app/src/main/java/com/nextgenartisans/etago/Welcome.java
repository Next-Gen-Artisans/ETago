package com.nextgenartisans.etago;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Welcome extends AppCompatActivity {

    AppCompatButton welcomeLoginBtn, welcomeSignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        setContentView(R.layout.activity_welcome);

        welcomeLoginBtn = findViewById(R.id.welcome_login_btn);
        welcomeSignUpBtn = findViewById(R.id.welcome_signup_btn);

        welcomeLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Welcome.this, LoginActivity.class);
                startActivity(i);
                finish();

            }
        });

        welcomeSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Welcome.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}