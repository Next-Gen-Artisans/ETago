package com.nextgenartisans.etago.login_signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.dialogs.ForgotPassDialog;

public class ForgotPass extends AppCompatActivity {

    LinearLayout forgotPassHeader, forgotPassBody;
    ImageButton forgotPassBackBtn;

    TextInputLayout forgotPassEmailInput;
    TextInputEditText forgotPassUserEmail;
    AppCompatButton sendPassResBtn, cancelPassResBtn;
    ProgressBar forgotPassProgBar;

    FirebaseAuth firebaseAuth;
    private Handler handler = new Handler();
    private static final int CHECK_INTERVAL = 5000; // Check every 5 seconds

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
        setContentView(R.layout.activity_forgot_pass);

        //Set id for clickable text and buttons
        forgotPassHeader = findViewById(R.id.forgot_pass_header);
        forgotPassBody = findViewById(R.id.forgot_pass_body);
        forgotPassBackBtn = findViewById(R.id.forgot_pass_back_btn);
        forgotPassEmailInput = findViewById(R.id.forgot_pass_email_input);
        forgotPassUserEmail = findViewById(R.id.forgot_pass_user_email);
        sendPassResBtn = findViewById(R.id.send_pass_res_btn);
        forgotPassProgBar = findViewById(R.id.forgot_pass_progbar);
        cancelPassResBtn = findViewById(R.id.cancel_pass_res_btn);


        //Remove animation when input text is focused by user
        forgotPassUserEmail.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                forgotPassEmailInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (forgotPassUserEmail.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    forgotPassEmailInput.setHint("Email");
                }
            }
        });

        forgotPassBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgotPass.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        cancelPassResBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ForgotPass.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        // Initialize FirebaseAuth for Password Reset
        firebaseAuth = FirebaseAuth.getInstance();
        sendPassResBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = forgotPassUserEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(ForgotPass.this, "Please enter your email address", Toast.LENGTH_SHORT).show();
                } else {
                    // Show progress bar
                    forgotPassProgBar.setVisibility(View.VISIBLE);
                    forgotPassUserEmail.setEnabled(false);

                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // Hide progress bar
                            forgotPassProgBar.setVisibility(View.INVISIBLE);
                            forgotPassUserEmail.setEnabled(true);

                            if (task.isSuccessful()) {

                                //Show Dialog
                                ForgotPassDialog forgotPassDialog = new ForgotPassDialog(ForgotPass.this);
                                forgotPassDialog.show();


                            } else {
                                Toast.makeText(ForgotPass.this, "Failed to send reset email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });


    }


}