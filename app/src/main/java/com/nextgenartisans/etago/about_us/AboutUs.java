package com.nextgenartisans.etago.about_us;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nextgenartisans.etago.dialogs.LogoutDialog;
import com.nextgenartisans.etago.feedback.Feedback;
import com.nextgenartisans.etago.home.MainActivity;
import com.nextgenartisans.etago.login_signup.LoginActivity;
import com.nextgenartisans.etago.profile.ProfileActivity;
import com.nextgenartisans.etago.settings.SettingsActivity;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;


public class AboutUs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


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

    @Override
    protected void onStart() {
        super.onStart();
        // Call the method to load user data when the activity starts
        loadUserData();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.light_blue));
        }

        setContentView(R.layout.activity_about_us);

        //Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbarProfile = findViewById(R.id.custom_toolbar_profile);
        profileHeader = findViewById(R.id.about_us_header);

        //Navigation View
        profileNavView = findViewById(R.id.about_nav_view);

        //TextViews
        headerView = profileNavView.getHeaderView(0);
        profileUserPicHeader = (ShapeableImageView) headerView.findViewById(R.id.drawer_user_profile_pic);
        profileUsernameHeader = (TextView) headerView.findViewById(R.id.drawer_username);
        profileEmailHeader = (TextView) headerView.findViewById(R.id.drawer_user_email);

        // Button Click Listeners

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

        MenuItem homeItem = profileNavView.getMenu().findItem(R.id.nav_about1);
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


        ImageView nextButtonNGA = findViewById(R.id.nga_next);
        ImageView nextButtonMission = findViewById(R.id.next_mission);
        ImageView nextButtonVision = findViewById(R.id.next_vision);
        ImageView nextButtonEtago = findViewById(R.id.next_etago);
        ImageView nextButtonTeam = findViewById(R.id.next_team);

        ImageView backButtonMission = findViewById(R.id.back_mission);
        ImageView backButtonVision = findViewById(R.id.back_vision);
        ImageView backButtonEtago = findViewById(R.id.back_etago);
        ImageView backButtonTeam = findViewById(R.id.back_team);


        LinearLayout NGAlayout = findViewById(R.id.NGA_first_layout);
        LinearLayout hiddenMission = findViewById(R.id.about_us_Mission);
        LinearLayout hiddenVision = findViewById(R.id.vision_layout);
        LinearLayout hiddenEtago = findViewById(R.id.about_us_ETago);
        LinearLayout hiddenTeam = findViewById(R.id.team);


        nextButtonNGA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenMission.setVisibility(View.VISIBLE);
                NGAlayout.setVisibility(View.GONE);
                nextButtonMission.setVisibility(View.VISIBLE);
            }
        });

        nextButtonMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenVision.setVisibility(View.VISIBLE);
                hiddenMission.setVisibility(View.GONE);
                nextButtonVision.setVisibility(View.VISIBLE);
            }
        });

        backButtonMission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenMission.setVisibility(View.GONE);
                NGAlayout.setVisibility(View.VISIBLE);
            }
        });

        nextButtonVision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenEtago.setVisibility(View.VISIBLE);
                hiddenVision.setVisibility(View.GONE);
                nextButtonVision.setVisibility(View.GONE);
                nextButtonEtago.setVisibility(View.VISIBLE);
            }
        });

        backButtonVision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenVision.setVisibility(View.GONE);
                hiddenMission.setVisibility(View.VISIBLE);
                nextButtonVision.setVisibility(View.GONE);
                nextButtonMission.setVisibility(View.VISIBLE);
            }
        });


        nextButtonEtago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenTeam.setVisibility(View.VISIBLE);
                hiddenEtago.setVisibility(View.GONE);
                nextButtonEtago.setVisibility(View.GONE);
                nextButtonTeam.setVisibility(View.VISIBLE);
            }
        });

        backButtonEtago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenEtago.setVisibility(View.GONE);
                hiddenVision.setVisibility(View.VISIBLE);
                nextButtonEtago.setVisibility(View.GONE);
                nextButtonVision.setVisibility(View.VISIBLE);
            }
        });

        nextButtonTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenTeam.setVisibility(View.GONE);
                NGAlayout.setVisibility(View.VISIBLE);
                nextButtonNGA.setVisibility(View.VISIBLE);
            }
        });

        backButtonTeam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hiddenTeam.setVisibility(View.GONE);
                hiddenEtago.setVisibility(View.VISIBLE);
                nextButtonEtago.setVisibility(View.VISIBLE);
            }
        });
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
            Glide.with(AboutUs.this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.round_bg) // Replace with your default image
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
        if (id == R.id.nav_about1) {
            return true;
        }
        if (id == R.id.nav_home1) {
            Intent intent = new Intent(AboutUs.this, MainActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_settings1) {
            Intent intent = new Intent(AboutUs.this, SettingsActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_profile1) {
            Intent intent = new Intent(AboutUs.this, ProfileActivity.class);
            startActivity(intent);
        }
        if (id == R.id.nav_feedback) {
            //navigationView.setCheckedItem(R.id.nav_about1);
            Intent intent = new Intent(AboutUs.this, Feedback.class);
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


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
