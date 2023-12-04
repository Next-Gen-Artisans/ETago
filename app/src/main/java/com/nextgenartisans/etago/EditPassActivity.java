package com.nextgenartisans.etago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EditPassActivity extends AppCompatActivity {

    // Declare the views
    private CardView editPassHeader;
    private LinearLayout editPassContainers, editPassHeaderContainer;
    private ImageButton editPassBackBtn;
    private TextView editPassHeaderText, forgotPass;
    private TextInputLayout editPassInputLayout1, editPassInputLayout2, editPassInputLayout3;
    private TextInputEditText editPassPassInput, editPassNewInput, editPassConfirmInput;
    private AppCompatButton updatePassBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_edit_pass);

        // Initialize the views
        editPassHeader = findViewById(R.id.edit_pass_header);
        editPassContainers = findViewById(R.id.edit_pass_containers);
        editPassHeaderContainer = findViewById(R.id.edit_pass_header_container);
        editPassHeaderText = findViewById(R.id.edit_pass_header_txt);

        //Buttons
        editPassBackBtn = findViewById(R.id.edit_pass_back_btn);
        forgotPass = findViewById(R.id.forgot_pass);
        updatePassBtn = findViewById(R.id.update_pass_btn);

        //Edit Texts Layouts
        editPassInputLayout1 = findViewById(R.id.edit_pass_input_1);
        editPassInputLayout2 = findViewById(R.id.edit_pass_input_2);
        editPassInputLayout3 = findViewById(R.id.edit_pass_input_3);

        //Edit Texts
        editPassPassInput = findViewById(R.id.edit_pass_pass_input);
        editPassNewInput = findViewById(R.id.edit_pass_new_input);
        editPassConfirmInput = findViewById(R.id.edit_pass_confirm_input);

        //Remove animation when input text is focused by user
        editPassPassInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editPassInputLayout1.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editPassPassInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editPassInputLayout1.setHint("Password");
                }
            }
        });

        //Remove animation when input text is focused by user
        editPassNewInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editPassInputLayout2.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editPassNewInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editPassInputLayout2.setHint("New Username");
                }
            }
        });

        //Remove animation when input text is focused by user
        editPassConfirmInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editPassInputLayout3.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editPassConfirmInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editPassInputLayout3.setHint("Confirm Username");
                }
            }
        });

        //BACK BUTTON TO PROFILE ACTIVITY
        editPassBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditPassActivity.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });





    }
}