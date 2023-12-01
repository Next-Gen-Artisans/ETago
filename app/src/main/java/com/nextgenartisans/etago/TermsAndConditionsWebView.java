package com.nextgenartisans.etago;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;

public class TermsAndConditionsWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions_webview);

        WebView webView = findViewById(R.id.web_view);
        String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);

    }
}