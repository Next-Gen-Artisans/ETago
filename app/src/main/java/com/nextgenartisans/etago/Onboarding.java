package com.nextgenartisans.etago;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Onboarding extends AppCompatActivity {


    ViewPager mSlideViewPager;
    LinearLayout mDotLayout;
    AppCompatButton skipBtn, nextBtn;
    ImageButton backBtn;
    TextView[] dots;
    OnboardingViewPagerAdapter onboardingViewPagerAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        // Check if Onboarding is already completed
        SharedPreferences preferences = getSharedPreferences("OnboardingPrefs", MODE_PRIVATE);
        boolean onboardingCompleted = preferences.getBoolean("onboarding_completed", false);

        if (onboardingCompleted) {
            // Onboarding already completed, skip it
            navigateToWelcome();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        skipBtn = findViewById(R.id.skip_btn);
        nextBtn = findViewById(R.id.next_btn);
        backBtn = findViewById(R.id.back_btn);

        // Set backBtn to INVISIBLE initially
        backBtn.setVisibility(View.INVISIBLE);

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Set Onboarding as completed
                preferences.edit().putBoolean("onboarding_completed", true).apply();
                navigateToWelcome();

            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getItem(0)<3){
                    mSlideViewPager.setCurrentItem(getItem(1),true);
                }
                else {
                    // Set Onboarding as completed
                    preferences.edit().putBoolean("onboarding_completed", true).apply();
                    navigateToWelcome();
                }

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(getItem(0)>0){
                    mSlideViewPager.setCurrentItem(getItem(-1),true);
                }


            }
        });

        mSlideViewPager = (ViewPager) findViewById(R.id.slide_view_pager);
        mDotLayout = (LinearLayout) findViewById(R.id.indicator_layout);

        onboardingViewPagerAdapter = new OnboardingViewPagerAdapter(this);

        mSlideViewPager.setAdapter(onboardingViewPagerAdapter);

        setUpIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

    }

    public void setUpIndicator(int pos){
        dots = new TextView[4];
        mDotLayout.removeAllViews();

        for (int i = 0; i< dots.length;i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                dots[i].setTextColor(getResources().getColor(R.color.white, getApplicationContext().getTheme()));
            }
            mDotLayout.addView(dots[i]);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            dots[pos].setTextColor(getResources().getColor(R.color.orange, getApplicationContext().getTheme()));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            setUpIndicator(position);

            // Check if it's the last view pager
            if (position == onboardingViewPagerAdapter.getCount() - 1) {
                // Set the text of nextBtn to "Get Started"
                nextBtn.setText("Get Started");
            } else {
                // Set the text of nextBtn to "Next" for other pages
                nextBtn.setText("Next");
            }

            // Toggle the visibility of backBtn based on position
            if(position > 0 ){
                backBtn.setVisibility(View.VISIBLE);
            }
            else{
                backBtn.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private int getItem(int i){
        return mSlideViewPager.getCurrentItem() + i;
    }

    private void navigateToWelcome() {
        Intent i = new Intent(Onboarding.this, Welcome.class);
        startActivity(i);
        finish();
    }


}