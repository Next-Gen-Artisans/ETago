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

public class EditEmailActivity extends AppCompatActivity {

    // Declare the views
    private CardView editEmailHeader;
    private LinearLayout editEmailContainers, editEmailHeaderContainer;
    private ImageButton editEmailBackBtn;
    private TextView editEmailHeaderText;
    private TextInputLayout editEmailInputLayout1, editEmailInputLayout2, editEmailInputLayout3;
    private TextInputEditText editEmailPassInput, editEmailNewInput, editEmailConfirmInput;
    private AppCompatButton updateEmailBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_edit_email);

        // Initialize the views
        editEmailHeader = findViewById(R.id.edit_email_header);
        editEmailContainers = findViewById(R.id.edit_email_containers);
        editEmailHeaderContainer = findViewById(R.id.edit_email_header_container);
        editEmailHeaderText = findViewById(R.id.edit_email_header_txt);

        //Buttons
        editEmailBackBtn = findViewById(R.id.edit_email_back_btn);
        updateEmailBtn = findViewById(R.id.update_email_btn);

        //Edit Texts Layouts
        editEmailInputLayout1 = findViewById(R.id.edit_email_input_1);
        editEmailInputLayout2 = findViewById(R.id.edit_email_input_2);
        editEmailInputLayout3 = findViewById(R.id.edit_email_input_3);

        //Edit Texts
        editEmailPassInput = findViewById(R.id.edit_email_pass_input);
        editEmailNewInput = findViewById(R.id.edit_email_new_input);
        editEmailConfirmInput = findViewById(R.id.edit_email_confirm_input);


        //Remove animation when input text is focused by user
        editEmailPassInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editEmailInputLayout1.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editEmailPassInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editEmailInputLayout1.setHint("Password");
                }
            }
        });

        //Remove animation when input text is focused by user
        editEmailNewInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editEmailInputLayout2.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editEmailNewInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editEmailInputLayout2.setHint("New Username");
                }
            }
        });

        //Remove animation when input text is focused by user
        editEmailConfirmInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editEmailInputLayout3.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editEmailConfirmInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editEmailInputLayout3.setHint("Confirm Username");
                }
            }
        });

        //BACK BUTTON TO PROFILE ACTIVITY
        editEmailBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditEmailActivity.this, ProfileActivity.class);
                startActivity(i);
                finish();
            }
        });



    }
}