package com.nextgenartisans.etago.home;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

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
import com.nextgenartisans.etago.about_us.AboutAppActivity;
import com.nextgenartisans.etago.dialogs.ExitAppDialog;
import com.nextgenartisans.etago.dialogs.LogoutDialog;
import com.nextgenartisans.etago.dialogs.TermsOfServiceDialog;
import com.nextgenartisans.etago.login_signup.LoginActivity;
import com.nextgenartisans.etago.onboarding.Welcome;
import com.nextgenartisans.etago.profile.ProfileActivity;
import com.nextgenartisans.etago.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    FirebaseAuth auth;
    FirebaseUser user;

    // Get the navigation header view
    View headerView;

    // Find the TextViews by their IDs
    TextView usernameTextView, usernameHeaderTextView, cardViewCaptureTxt, cardViewUploadTxt;
    TextView emailTextView;
    ShapeableImageView userProfilePic;

    //Main Buttons
    CardView cardViewCapture, cardViewUpload;
    LinearLayout buttonContainers, cardViewCaptureContainer, cardViewUploadContainer;
    ImageButton captureImageButton, uploadImageButton;

    //Custom Dialogs
    LogoutDialog logoutDialog;
    ExitAppDialog exitAppDialog;
    TermsOfServiceDialog termsOfServiceDialog;

    //MEDIA PERMISSIONS
    public static final int STORAGE_PERMISSION_CODE = 1;

    //On Back Pressed Variables
    private boolean doubleBackToExitPressedOnce = false;
    private Handler handler = new Handler();

    //

    @Override
    protected void onStart() {
        super.onStart();
        // Call the method to load user data when the activity starts
        loadUserData();

        // Show terms of service dialog
        checkUserAgreement();


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

        setContentView(R.layout.activity_main);

        //Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Custom Dialogs
        logoutDialog = new LogoutDialog(MainActivity.this);
        exitAppDialog = new ExitAppDialog(MainActivity.this);
        termsOfServiceDialog = new TermsOfServiceDialog(MainActivity.this);

        //Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.custom_toolbar);

        // Inside onCreate method
        navigationView.setCheckedItem(R.id.nav_home1);

        //Containers
        cardViewCapture = findViewById(R.id.cardview_capture);
        cardViewUpload = findViewById(R.id.cardview_upload);
        buttonContainers = findViewById(R.id.button_containers);
        cardViewCaptureContainer = findViewById(R.id.cardview_capture_container);
        cardViewUploadContainer = findViewById(R.id.cardview_upload_container);


        //TextViews
        headerView = navigationView.getHeaderView(0);
        userProfilePic = (ShapeableImageView) headerView.findViewById(R.id.drawer_user_profile_pic);
        usernameTextView = (TextView) headerView.findViewById(R.id.drawer_username);
        emailTextView = (TextView) headerView.findViewById(R.id.drawer_user_email);
        usernameHeaderTextView = findViewById(R.id.user_name);
        cardViewCaptureTxt = findViewById(R.id.cardview_capture_txt);
        cardViewUploadTxt = findViewById(R.id.cardview_upload_txt);

        //Main Buttons
        captureImageButton = findViewById(R.id.capture_image_btn);
        uploadImageButton = findViewById(R.id.upload_image_btn);


        if (user == null) {
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            // Set the username and email based on the Firebase user's information
            loadUserData();
            loadUserProfilePicture();
            String username = user.getDisplayName(); // Replace with your user data
            String email = user.getEmail(); // Replace with your user data

            if (username != null) {
                usernameTextView.setText(username);
            } else {
                // Set a default value if username is null
                usernameTextView.setText("Username");
            }

            if (email != null) {
                emailTextView.setText(email);
            } else {
                // Set a default value if email is null
                emailTextView.setText("User Email");
            }

        }

        //TODO DISPLAY TERMS AND CONDITIONS, REQUEST PERMISSIONS


        //Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        //Navigation Drawer Menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false); // Disable the default hamburger icon
        toggle.setHomeAsUpIndicator(R.drawable.menu); // Set your custom icon
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        MenuItem homeItem = navigationView.getMenu().findItem(R.id.nav_home1);
        homeItem.setChecked(true);

        navigationView.setNavigationItemSelectedListener(this);

        // Add an OnClickListener to your custom icon
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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




        //TODO IMPLEMENT ANDROID APP
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UploadImg.class);
                startActivity(i);
                finish();
            }
        });


        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CaptureImg.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void checkUserAgreement() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DocumentReference docRef = db.collection("Users").document(currentUser.getUid());
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists() && documentSnapshot.contains("userAgreedTermsAndPrivacyPolicy")) {
                        boolean hasAgreed = documentSnapshot.getBoolean("userAgreedTermsAndPrivacyPolicy");
                        if (!hasAgreed) {
                            // User has not agreed yet, show the terms of service dialog
                            showTermsOfServiceDialog();
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "User did not agree to terms and privacy policy!");
                }
            });
        }
    }

    private void showTermsOfServiceDialog() {
        if (!termsOfServiceDialog.isShowing()) {
            termsOfServiceDialog.show();
            Toast.makeText(this, "Click \"I agree\" to proceed.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Click \"I agree\" to proceed.", Toast.LENGTH_SHORT).show();
        }

    }


    private void loadUserProfilePicture() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users") // Replace with your actual collection name
                .document(user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String imageUrl = documentSnapshot.getString("profilePic"); // Replace with your field name
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(MainActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.round_bg)
                                        .into(userProfilePic);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors
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

                        // Update the UI with the retrieved data
                        updateUI(profilePicUrl, username, email);
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

    private void updateUI(String profilePicUrl, String username, String email) {
        // Set the profile picture using Glide or another image loading library
        if (profilePicUrl != null && !profilePicUrl.equals("default_profile_pic_url")) {
            Glide.with(MainActivity.this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.round_bg) // Replace with your default image
                    .into(userProfilePic);
        } else {
            userProfilePic.setImageResource(R.drawable.round_bg); // Set default image
        }

        // Set the username and email
        usernameTextView.setText(username != null ? username : "Username");
        // Split the username to get the first name
        if (username != null && !username.isEmpty()) {
            String[] nameParts = username.split(" ");
            String firstName = nameParts[0] + "!";
            usernameHeaderTextView.setText(firstName);
        } else {
            usernameHeaderTextView.setText("Username");
        }

        emailTextView.setText(email != null ? email : "Email");


    }

    public void goToWelcome() {
        // Start the WelcomeActivity
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, Welcome.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home1) {
            return true;
        }
        if (id == R.id.nav_profile1) {
            //navigationView.setCheckedItem(R.id.nav_profile1);
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);


        }
        if (id == R.id.nav_settings1) {
            //navigationView.setCheckedItem(R.id.nav_settings1);
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);


        }
        if (id == R.id.nav_about1) {
            //navigationView.setCheckedItem(R.id.nav_about1);
            Intent intent = new Intent(MainActivity.this, AboutAppActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            //Show Dialog
            logoutDialog.show();

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            // Close the drawer if it's open
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //moveTaskToBack(true); // Move the task containing this activity to the back of the activity stack.
            exitAppDialog.show();
//            // If doubleBackToExitPressedOnce is true, finish the activity
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;
//            }
//
//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//            // If the user does not press back within 2 seconds, reset the flag
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    doubleBackToExitPressedOnce = false;
//                }
//            }, 2000);

        }
        super.onBackPressed();
    }


    // Function to get the height of the status bar
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}