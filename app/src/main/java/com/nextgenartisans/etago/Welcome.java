package com.nextgenartisans.etago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Welcome extends AppCompatActivity {

    AppCompatButton welcomeLoginBtn, welcomeSignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Make app full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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