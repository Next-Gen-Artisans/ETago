package com.nextgenartisans.etago.settings;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.nextgenartisans.etago.R;

public class HelpSupport extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ScrollView scrollView;
    private ConstraintLayout supportContainer;
    private Toolbar toolBar;
    private AppCompatTextView title;
    private CardView headerCardview;
    private NavigationView navView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help_support);

        drawerLayout = findViewById(R.id.drawer_layout);
        scrollView = findViewById(R.id.scroll_view);
        supportContainer = findViewById(R.id.constraint_container_help);
        toolBar = findViewById(R.id.custom_toolbar_profile);
        title = findViewById(R.id.settings_title);
        headerCardview = findViewById(R.id.settings_header);
        navView = findViewById(R.id.about_nav_view);

    }
}
