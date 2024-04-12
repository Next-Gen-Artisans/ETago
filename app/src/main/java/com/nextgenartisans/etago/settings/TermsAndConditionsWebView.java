package com.nextgenartisans.etago.settings;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

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



        webViewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        termsOfServiceDialog.dismiss();
        Toast.makeText(this, "Click \"I agree\" to proceed.", Toast.LENGTH_SHORT).show();
        finish();
        super.onBackPressed();
    }
}