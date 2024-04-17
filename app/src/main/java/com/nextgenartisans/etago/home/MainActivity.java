package com.nextgenartisans.etago.home;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.about_us.AboutUs;
import com.nextgenartisans.etago.dialogs.ExitAppDialog;
import com.nextgenartisans.etago.dialogs.LogoutDialog;
import com.nextgenartisans.etago.dialogs.MediaPermissionDialog;
import com.nextgenartisans.etago.dialogs.TermsOfServiceDialog;
import com.nextgenartisans.etago.feedback.FeedbackSurvey;
import com.nextgenartisans.etago.login_signup.LoginActivity;
import com.nextgenartisans.etago.onboarding.Welcome;
import com.nextgenartisans.etago.profile.NewPassActivity;
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
    MediaPermissionDialog mediaPermissionDialog;



    //MEDIA PERMISSIONS
    public static final int STORAGE_PERMISSION_CODE = 1;

    private static final int REQUEST_MEDIA_CAMERA_PERMISSION = 101;

    //On Back Pressed Variables
    private boolean doubleBackToExitPressedOnce = false;
    private Handler handler = new Handler();

    @Override
    protected void onStart() {
        super.onStart();

        // Call the method to load user data when the activity starts
        loadUserData();

        //Set New Password
        setNewPassword();

        // Check if password is set before showing dialogs
        checkPasswordAndShowDialogs();

    }

    private void checkPasswordAndShowDialogs() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userDoc = db.collection("Users").document(userId);
            userDoc.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    DocumentSnapshot document = task.getResult();
                    Boolean isPasswordSet = document.getBoolean("userPasswordSet");
                    if (Boolean.TRUE.equals(isPasswordSet)) {
                        // Check agreement and then permissions
                        checkUserAgreement();
                    }
                } else {
                    Log.d(TAG, "Error fetching user document:", task.getException());
                }
            });
        }
    }

    private void askMediaPermissions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DocumentReference docRef = db.collection("Users").document(currentUser.getUid());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.contains("userAgreedMedia")) {
                    boolean hasAgreed = documentSnapshot.getBoolean("userAgreedMedia");
                    if (!hasAgreed && !mediaPermissionDialog.isShowing()) {
                        mediaPermissionDialog.show();
                    }
                }
            }).addOnFailureListener(e -> Log.d(TAG, "Failed to retrieve media agreement status.", e));
        }
    }



    private boolean hasMediaPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, update Firestore
                updateFirestoreUserAgreedMedia(true);
            } else {
                // Permission denied
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateFirestoreUserAgreedMedia(boolean agreed) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DocumentReference userDoc = db.collection("Users").document(currentUser.getUid());
            userDoc.update("userAgreedMedia", agreed)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        }
    }


    private void setNewPassword() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Users").document(userId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null && document.exists()) {
                                    Boolean isPasswordSet = document.getBoolean("userPasswordSet");
                                    if (isPasswordSet == null || !isPasswordSet) {
                                        // Redirect to NewPassActivity
                                        Intent i = new Intent(getApplicationContext(), NewPassActivity.class);
                                        startActivity(i);
                                    }
                                } else {
                                    Log.d("Firestore", "No such document");
                                }
                            } else {
                                Log.d("Firestore", "get failed with ", task.getException());
                            }
                        }
                    });
        }
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

        // Check if a new user has signed up
        boolean newUser = getIntent().getBooleanExtra("newUser", false);
        if (newUser) {
            loadUserData();
        }

        //Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Custom Dialogs
        logoutDialog = new LogoutDialog(MainActivity.this);
        exitAppDialog = new ExitAppDialog(MainActivity.this);
        termsOfServiceDialog = new TermsOfServiceDialog(MainActivity.this);
        mediaPermissionDialog = new MediaPermissionDialog(MainActivity.this);

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

        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), UploadImg.class);
                startActivity(i);
            }
        });


        captureImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), CaptureImg.class);
                startActivity(i);
            }
        });

    }

    private void checkUserAgreement() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DocumentReference docRef = FirebaseFirestore.getInstance().collection("Users").document(currentUser.getUid());
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists() && documentSnapshot.contains("userAgreedTermsAndPrivacyPolicy")) {
                    boolean hasAgreed = documentSnapshot.getBoolean("userAgreedTermsAndPrivacyPolicy");
                    if (!hasAgreed) {
                        showTermsOfServiceDialog();
                    } else {
                        askMediaPermissions();
                    }
                } else {
                    // Assume no agreement if document or field is missing
                    showTermsOfServiceDialog();
                }
            }).addOnFailureListener(e -> {
                Log.d(TAG, "Failed to retrieve agreement status.", e);
                // Fallback to show dialog anyway if there is an error
                showTermsOfServiceDialog();
            });
        }
    }

    private void showTermsOfServiceDialog() {
        if (!termsOfServiceDialog.isShowing()) {
            termsOfServiceDialog.setOnDismissListener(dialog -> {
                // This callback will be triggered when the dialog is dismissed
                askMediaPermissions();  // Now check for media permissions
            });
            termsOfServiceDialog.show();
        }
    }

    private void showMediaPermissionDialog() {
        if (!mediaPermissionDialog.isShowing()) {
            mediaPermissionDialog.show();
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
                                        .centerCrop()
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
                    .centerCrop()
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
            finish();

        }
        if (id == R.id.nav_settings1) {
            //navigationView.setCheckedItem(R.id.nav_settings1);
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();

        }
        if (id == R.id.nav_about1) {
            //navigationView.setCheckedItem(R.id.nav_about1);
            Intent intent = new Intent(MainActivity.this, AboutUs.class);
            startActivity(intent);
            finish();

        }
        if (id == R.id.nav_feedback) {
            //navigationView.setCheckedItem(R.id.nav_about1);
            Intent intent = new Intent(MainActivity.this, FeedbackSurvey.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_logout) {
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