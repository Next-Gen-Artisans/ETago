package com.nextgenartisans.etago.settings;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.about_us.AboutUs;
import com.nextgenartisans.etago.dialogs.LogoutDialog;
import com.nextgenartisans.etago.feedback.FeedbackSurvey;
import com.nextgenartisans.etago.home.MainActivity;
import com.nextgenartisans.etago.login_signup.LoginActivity;
import com.nextgenartisans.etago.profile.ProfileActivity;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Navigation Drawer
    private DrawerLayout drawerLayout;
    private NavigationView profileNavView;
    private Toolbar toolbarProfile;
    private CardView profileHeader;

    // Get the navigation header view items
    View headerView;
    TextView profileUsernameHeader, profileEmailHeader;
    ShapeableImageView profileUserPicHeader;

    //Custom Dialogs
    LogoutDialog logoutDialog;

    //Firebase
    FirebaseAuth auth;
    FirebaseUser user;

    //Buttons for Terms of Service and Privacy Policy
    private ImageView termsOfService, privacyPolicy, helpSupport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_settings);

        //Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbarProfile = findViewById(R.id.custom_toolbar_profile);
        profileHeader = findViewById(R.id.settings_header);


        //Navigation View
        profileNavView = findViewById(R.id.about_nav_view);

        //TextViews
        headerView = profileNavView.getHeaderView(0);
        profileUserPicHeader = (ShapeableImageView) headerView.findViewById(R.id.drawer_user_profile_pic);
        profileUsernameHeader = (TextView) headerView.findViewById(R.id.drawer_username);
        profileEmailHeader = (TextView) headerView.findViewById(R.id.drawer_user_email);

        //Buttons for Terms of Service and Privacy Policy
        termsOfService = findViewById(R.id.square_terms);
        privacyPolicy = findViewById(R.id.square_priv);
        helpSupport = findViewById(R.id.square_help);


        if (user == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            // Set the username and email based on the Firebase user's information
            loadUserData();
            String username = user.getDisplayName(); // Replace with your user data
            String email = user.getEmail(); // Replace with your user data

        }

        //Toolbar
        setSupportActionBar(toolbarProfile);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //Navigation Drawer Menu
        profileNavView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbarProfile, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false); // Disable the default hamburger icon
        toggle.setHomeAsUpIndicator(R.drawable.menu); // Set your custom icon
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        MenuItem homeItem = profileNavView.getMenu().findItem(R.id.nav_settings1);
        homeItem.setChecked(true);

        profileNavView.setNavigationItemSelectedListener(this);

        // Add an OnClickListener to your custom icon
        toolbarProfile.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    // Close the drawer if it's open
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Open the drawer if it's closed
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        //Add on OnCLick Listener to the Terms of Service and Privacy Policy Buttons to open the WebView
        termsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView("https://drive.google.com/file/d/1gsOzWWpFXeKpeeb5aZ5OsB1QfXCZDI6D/view?usp=drive_link", "Terms of Service");
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView("https://drive.google.com/file/d/1ecGdBb3ygro_43CvgehtJ6DNcljnC03O/view?usp=drive_link", "Privacy Policy");
            }
        });

        helpSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWebView("https://docs.google.com/presentation/d/1g_tPeQyOG23fTMADdxPZLlSCvpGkdJd-xtvfUOErvWY/edit?usp=sharing", "Help and Support");
            }
        });
    }

    private void openWebView(String url, String webViewText) {
        Intent intent = new Intent(this, TermsAndConditionsWebView.class);
        intent.putExtra("url", url);
        intent.putExtra("webViewText", webViewText);
        this.startActivity(intent);
    }

    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("Users").document(currentUser.getUid());

            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        // Extract the profile picture, username, and email
                        String profilePicUrl = documentSnapshot.getString("profilePic");
                        String username = documentSnapshot.getString("username");
                        String email = documentSnapshot.getString("email");
                        int numCensoredImgs = documentSnapshot.contains("numCensoredImgs") ? documentSnapshot.getLong("numCensoredImgs").intValue() : 0;

                        // Update the UI with the retrieved data
                        updateUI(profilePicUrl, username, email, numCensoredImgs);
                    } else {
                        // Document does not exist, handle this case
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Handle the error
                }
            });
        }
    }

    private void updateUI(String profilePicUrl, String username, String email, int numCensoredImages) {
        // Set the profile picture using Glide or another image loading library
        if (profilePicUrl != null && !profilePicUrl.equals("default_profile_pic_url")) {
            Glide.with(SettingsActivity.this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.round_bg) // Replace with your default image
                    .centerCrop()
                    .into(profileUserPicHeader);
        } else {
            profileUserPicHeader.setImageResource(R.drawable.round_bg); // Set default image
        }

        // Set the username and email
        profileUsernameHeader.setText(username != null ? username : "Username");
        profileEmailHeader.setText(email != null ? email : "Email");

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_settings1) {
            return true;
        }
        if (id == R.id.nav_home1) {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.nav_about1) {
            Intent intent = new Intent(SettingsActivity.this, AboutUs.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.nav_profile1) {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
            finish();

        }if (id == R.id.nav_feedback) {
            Intent intent = new Intent(SettingsActivity.this, FeedbackSurvey.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout) {
            //Show Dialog
            logoutDialog = new LogoutDialog(this);
            logoutDialog.show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void surveywebview(){
        openWebView("https://docs.google.com/forms/d/e/1FAIpQLScOCEo5ro0vtuXZhJz66y4BhIfkR1fHmY0wYh8b6Unx0fsZfw/viewform?usp=sharing", "Survey");

    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


}