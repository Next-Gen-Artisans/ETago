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
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.about_us.AboutUs;
import com.nextgenartisans.etago.dialogs.TermsOfServiceDialog;
import com.nextgenartisans.etago.home.MainActivity;
import com.nextgenartisans.etago.settings.SettingsActivity;
import com.nextgenartisans.etago.settings.TermsAndConditionsWebView;

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

        String url = "https://docs.google.com/forms/d/e/1FAIpQLScOCEo5ro0vtuXZhJz66y4BhIfkR1fHmY0wYh8b6Unx0fsZfw/viewform?usp=sharing";
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
