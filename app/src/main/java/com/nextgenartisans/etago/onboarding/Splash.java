package com.nextgenartisans.etago.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nextgenartisans.etago.home.MainActivity;
import com.nextgenartisans.etago.R;

public class Splash extends AppCompatActivity {

    private static final String PREFS_NAME = "SplashPrefs";
    private static final String KEY_FIRST_TIME = "FirstTime";
    private static final int CHECK_INTERVAL = 5000; // Check every 5 seconds
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_splash);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        if (preferences.getBoolean(KEY_FIRST_TIME, true)) {
            // First time running app
            checkInternetConnection();
        } else {
            // Not the first time, skip check
            // Check if the user is already logged in using Firebase Authentication
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                // User is logged in, navigate directly to the Main Activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                navigateToNextScreen();
            }
        }
    }

    private void checkInternetConnection() {
        if (isConnectedToInternet()) {
            // Internet is available
            SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            preferences.edit().putBoolean(KEY_FIRST_TIME, false).apply();
            navigateToNextScreen();
        } else {
            // No internet connection, schedule the next check
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkInternetConnection();
                }
            }, CHECK_INTERVAL);

            // Optionally, you can show a toast to inform the user that the app is waiting for internet connectivity.
            Toast.makeText(this, "Waiting for Internet Connection...", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToNextScreen() {
        handler.removeCallbacksAndMessages(null); // Remove any pending checks
        Intent i = new Intent(Splash.this, Onboarding.class);
        startActivity(i);
        finish();
    }

    private boolean isConnectedToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
