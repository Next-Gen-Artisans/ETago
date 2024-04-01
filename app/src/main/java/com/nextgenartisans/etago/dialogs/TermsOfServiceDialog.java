package com.nextgenartisans.etago.dialogs;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.settings.TermsAndConditionsWebView;

public class TermsOfServiceDialog extends Dialog {
    LinearLayout termsPrivacyLayout;
    TextView termsText, privacyText;
    AppCompatButton agreeTermsDialogBtn;


    public TermsOfServiceDialog(@NonNull Context context) {
        super(context);
        setCancelable(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_terms_of_service_dialog);

        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        termsPrivacyLayout = findViewById(R.id.terms_privacy_layout);
        termsText = findViewById(R.id.terms_text);
        privacyText = findViewById(R.id.privacy_text);
        agreeTermsDialogBtn = findViewById(R.id.agree_terms_dialog_btn);

        agreeTermsDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the Firestore document for the user to indicate they have agreed
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    DocumentReference userDocRef = db.collection("Users").document(currentUser.getUid());
                    userDocRef.update("userAgreedTermsAndPrivacyPolicy", true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successfully updated the user's agreement status
                                    dismiss();
                                    // Proceed with the main activity or other logic after the user has agreed
                                    Toast.makeText(getContext(), "You have agreed to E-Tago's terms and conditions.", Toast.LENGTH_LONG).show();
                                    Log.d(TAG,"USER AGREED ON DIALOG");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "You have not agreed to E-Tago's terms and conditions due to app error.", Toast.LENGTH_LONG).show();
                                    Log.d(TAG,"USER DID NOT AGREE ON DIALOG");
                                }
                            });
                }
            }
        });

        termsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView("https://drive.google.com/file/d/1gsOzWWpFXeKpeeb5aZ5OsB1QfXCZDI6D/view?usp=drive_link", "Terms of Service");
            }
        });

        privacyText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView("https://drive.google.com/file/d/1ecGdBb3ygro_43CvgehtJ6DNcljnC03O/view?usp=drive_link", "Privacy Policy");
            }
        });

    }

    private void openWebView(String url, String webViewText) {
        Intent intent = new Intent(getContext(), TermsAndConditionsWebView.class);
        intent.putExtra("url", url);
        intent.putExtra("webViewText", webViewText);
        getContext().startActivity(intent);
    }


}
