package com.nextgenartisans.etago.onboarding;

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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nextgenartisans.etago.MainActivity;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.dialogs.TermsOfServiceDialog;

public class TermsAndConditionsWebView extends AppCompatActivity {

    // Declare the variables for the views
    private CardView homeHeader;
    private LinearLayout webViewHeaderContainer, termsPolicyContainers, cardviewWebViewContainer;
    private ImageButton webViewBackBtn;
    private TextView webViewHeaderTxt;
    private WebView webView;
    private AppCompatButton webViewAgreeBtn;

    private TermsOfServiceDialog termsOfServiceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_terms_and_conditions_webview);

        // Initialize the variables with the corresponding views from the layout
        homeHeader = findViewById(R.id.home_header);
        termsPolicyContainers = findViewById(R.id.terms_policy_containers);
        webViewHeaderContainer = findViewById(R.id.web_view_header_container);
        webViewBackBtn = findViewById(R.id.web_view_back_btn);
        webViewHeaderTxt = findViewById(R.id.web_view_header_txt);
        cardviewWebViewContainer = findViewById(R.id.cardview_web_view_container);
        webView = findViewById(R.id.web_view);
        //webViewAgreeBtn = findViewById(R.id.webview_agree_btn);

        termsOfServiceDialog = new TermsOfServiceDialog(TermsAndConditionsWebView.this);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript
        webView.setWebViewClient(new WebViewClient()); // Add this line
        String url = getIntent().getStringExtra("url");
        String webViewText = getIntent().getStringExtra("webViewText");

        //Set Dynamic Components
        webViewHeaderTxt.setText(webViewText);
        webView.loadUrl(url);

//        webViewAgreeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Update the Firestore document for the user to indicate they have agreed
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//                if (currentUser != null) {
//                    DocumentReference userDocRef = db.collection("Users").document(currentUser.getUid());
//                    userDocRef.update("userAgreedTermsAndPrivacyPolicy", true)
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//                                    // Successfully updated the user's agreement status
//                                    Toast.makeText(getApplicationContext(), "You agreed to Terms and Conditions.", Toast.LENGTH_SHORT).show();
//                                    termsOfServiceDialog.dismiss();
//                                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
//                                    startActivity(i);
//                                    finish();
//
//                                    // Proceed with the main activity or other logic after the user has agreed
//                                    Log.d(TAG,"USER AGREED ON DIALOG");
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    Log.d(TAG,"USER DID NOT AGREE ON DIALOG");
//                                }
//                            });
//                }
//            }
//        });

        webViewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        termsOfServiceDialog.dismiss();
        Toast.makeText(this, "Click \"I agree\" to proceed.", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }
}