package com.nextgenartisans.etago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

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

public class EditUsernameActivity extends AppCompatActivity {

    // Declare the views
    private CardView editUsernameHeader;
    private LinearLayout editUsernameContainers, editUsernameHeaderContainer;
    private ImageButton editUsernameBackBtn;
    private TextView editUsernameHeaderText;
    private TextInputLayout editUsernameInputLayout1, editUsernameInputLayout2, editUsernameInputLayout3;
    private TextInputEditText editUsernamePassInput, editUsernameNewInput, editUsernameConfirmInput;
    private AppCompatButton updateUsernameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_edit_username);

        // Initialize the views
        editUsernameHeader = findViewById(R.id.edit_username_header);
        editUsernameContainers = findViewById(R.id.edit_username_containers);
        editUsernameHeaderContainer = findViewById(R.id.edit_username_header_container);
        editUsernameHeaderText = findViewById(R.id.edit_username_header_txt);

        //Buttons
        editUsernameBackBtn = findViewById(R.id.edit_username_back_btn);
        updateUsernameBtn = findViewById(R.id.update_username_btn);

        //Edit Texts Layouts
        editUsernameInputLayout1 = findViewById(R.id.edit_username_input_1);
        editUsernameInputLayout2 = findViewById(R.id.edit_username_input_2);
        editUsernameInputLayout3 = findViewById(R.id.edit_username_input_3);

        //Edit Texts
        editUsernamePassInput = findViewById(R.id.edit_username_pass_input);
        editUsernameNewInput = findViewById(R.id.edit_username_new_input);
        editUsernameConfirmInput = findViewById(R.id.edit_username_confirm_input);



        //Remove animation when input text is focused by user
        editUsernamePassInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editUsernameInputLayout1.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editUsernamePassInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editUsernameInputLayout1.setHint("Password");
                }
            }
        });

        //Remove animation when input text is focused by user
        editUsernameNewInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editUsernameInputLayout2.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editUsernameNewInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editUsernameInputLayout2.setHint("New Username");
                }
            }
        });

        //Remove animation when input text is focused by user
        editUsernameConfirmInput.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                editUsernameInputLayout3.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (editUsernameConfirmInput.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    editUsernameInputLayout3.setHint("Confirm Username");
                }
            }
        });

        editUsernameBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        editUsernameBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


    }
}