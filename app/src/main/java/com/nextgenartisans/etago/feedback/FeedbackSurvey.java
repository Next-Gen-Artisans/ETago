package com.nextgenartisans.etago.feedback;

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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.home.MainActivity;

public class FeedbackSurvey extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private LinearLayout webViewHeaderContainer, termsPolicyContainers, cardviewWebViewContainer;
    private ScrollView scrollView;
    private ConstraintLayout supportContainer;

    private AppCompatTextView title;
    private CardView homeHeader;
    private WebView webView;
    private ImageButton webViewBackBtn;
    private TextView webViewHeaderTxt;

    private NavigationView navView;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_feedback_webview);
        homeHeader = findViewById(R.id.home_header);
        termsPolicyContainers = findViewById(R.id.terms_policy_containers);
        webViewHeaderContainer = findViewById(R.id.web_view_header_container);
        webViewBackBtn = findViewById(R.id.web_view_back_btn);
        webViewHeaderTxt = findViewById(R.id.web_view_header_txt);
        cardviewWebViewContainer = findViewById(R.id.cardview_web_view_container);
        webView = findViewById(R.id.web_view);

        webView = findViewById(R.id.web_view);

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Set WebViewClient to keep navigation within the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;  // Returning false means that you are going to load this URL in the WebView
            }
        });

        String url = "https://docs.google.com/forms/d/e/1FAIpQLSfkof_nPqmKq3lhPdOC_gb1NuM64MaAfWx0diUBdHl6NZpQ8g/viewform?usp=sharing";
        webView.loadUrl(url);


        webViewBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}
