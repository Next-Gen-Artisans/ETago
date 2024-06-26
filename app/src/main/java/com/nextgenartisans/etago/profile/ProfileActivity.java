package com.nextgenartisans.etago.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.about_us.AboutUs;
import com.nextgenartisans.etago.dialogs.CustomDeleteAccDialog;
import com.nextgenartisans.etago.dialogs.LogoutDialog;
import com.nextgenartisans.etago.feedback.FeedbackSurvey;
import com.nextgenartisans.etago.home.MainActivity;
import com.nextgenartisans.etago.login_signup.LoginActivity;
import com.nextgenartisans.etago.settings.SettingsActivity;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ScrollView scrollView;
    private LinearLayout profileInfoContainers;
    private Toolbar toolbarProfile;
    private CardView profileHeader, profileUsernameCardView, profileEmailCardView, profilePassCardView;
    private ShapeableImageView profileUserPic, profileEditUsername, profileEditEmail, profileEditPass;
    private TextView numCensoredImgs, profileUsername, profileEmail, profilePass;
    private View divider;
    private AppCompatButton profileInfoLogoutBtn, deleteAccountBtn, openStatsBtn;
    private ImageButton profileEditPic;
    private NavigationView profileNavView;

    //Photo Picker
    private ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

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
        loadUserProfilePicture();
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

        setContentView(R.layout.activity_profile);



        //Firebase
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // Initialize Views
        drawerLayout = findViewById(R.id.drawer_layout);
        profileInfoContainers = findViewById(R.id.profile_info_containers);
        toolbarProfile = findViewById(R.id.custom_toolbar_profile);
        profileHeader = findViewById(R.id.profile_header);
        profileUserPic = findViewById(R.id.profile_user_pic);
        numCensoredImgs = findViewById(R.id.num_censored_imgs);
        divider = findViewById(R.id.divider);


        //Cardviews
        profileUsernameCardView = findViewById(R.id.profile_username_cardview);
        profileUsername = findViewById(R.id.profile_username);
        profileEmailCardView = findViewById(R.id.profile_email_cardview);
        profileEmail = findViewById(R.id.profile_email);
        profilePass = findViewById(R.id.profile_pass);
        profilePassCardView = findViewById(R.id.profile_pass_cardview);


        //Buttons
        openStatsBtn = findViewById(R.id.open_stats_btn);
        deleteAccountBtn = findViewById(R.id.delete_account_btn);
        profileInfoLogoutBtn = findViewById(R.id.profile_info_logout_btn);
        profileEditUsername = findViewById(R.id.profile_edit_username);
        profileEditEmail = findViewById(R.id.profile_edit_email);
        profileEditPass = findViewById(R.id.profile_edit_pass);
        profileEditPic = findViewById(R.id.profile_edit_pic);


        //Navigation View
        profileNavView = findViewById(R.id.profile_nav_view);
        //TextViews
        headerView = profileNavView.getHeaderView(0);
        profileUserPicHeader = (ShapeableImageView) headerView.findViewById(R.id.drawer_user_profile_pic);
        profileUsernameHeader = (TextView) headerView.findViewById(R.id.drawer_username);
        profileEmailHeader = (TextView) headerView.findViewById(R.id.drawer_user_email);


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
                profileUsername.setText(username);
            } else {
                // Set a default value if username is null
                profileUsername.setText("Username");
            }

            if (email != null) {
                profileEmail.setText(email);
            } else {
                // Set a default value if email is null
                profileEmail.setText("User Email");
            }

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

        MenuItem homeItem = profileNavView.getMenu().findItem(R.id.nav_profile1);
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

        openStatsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });

        deleteAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to confirm the deletion of the account
                showDeleteAccountDialog();
            }
        });

        profileInfoLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog = new LogoutDialog(ProfileActivity.this);
                logoutDialog.show();
            }
        });

        profileEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditUsernameActivity.class);
                startActivity(intent);
            }
        });

        profileEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditEmailActivity.class);
                startActivity(intent);
            }
        });

        profileEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EditPassActivity.class);
                startActivity(intent);
            }
        });

        //Change profile pic:
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        // Use Glide to load the image, resize it to fit the ImageView and center crop
                        Glide.with(ProfileActivity.this)
                                .load(uri)
                                .override(profileUserPic.getWidth(), profileUserPic.getHeight())
                                .centerCrop()
                                .into(profileUserPic);
                        Log.d("PhotoPicker", "Selected URI: " + uri);

                        // Initialize Firebase Storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference();

                        // Create a reference to the file to upload
                        StorageReference profilePicRef = storageRef.child("profilePics/" + user.getUid() + ".jpg");

                        // Upload the file to Firebase Storage
                        profilePicRef.putFile(uri)
                                .addOnSuccessListener(taskSnapshot -> {
                                    // Get the download URL of the uploaded file
                                    profilePicRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                                        // Update the profilePic field in the Firebase User collection
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                                        DocumentReference docRef = db.collection("Users").document(user.getUid());
                                        docRef.update("profilePic", downloadUri.toString())
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                                                    // Load the updated profile picture into the ImageView
                                                    Glide.with(ProfileActivity.this)
                                                            .load(downloadUri.toString())
                                                            .centerCrop()
                                                            .into(profileUserPic);
                                                    // Load the updated profile picture into the navigation bar ImageView
                                                    Glide.with(ProfileActivity.this)
                                                            .load(downloadUri.toString())
                                                            .centerCrop()
                                                            .into(profileUserPicHeader);
                                                })
                                                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
                                    });
                                })
                                .addOnFailureListener(e -> Log.w("Firebase Storage", "Error uploading file", e));
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        profileEditPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the photo picker activity
                pickMedia.launch(new PickVisualMediaRequest.Builder().build());


            }
        });

    }



    private void showDeleteAccountDialog() {
        // Create an instance of the CustomDeleteAccDialog
        CustomDeleteAccDialog deleteDialog = new CustomDeleteAccDialog(ProfileActivity.this);
        deleteDialog.show(); // Display the dialog
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
                                Glide.with(ProfileActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.round_bg)
                                        .centerCrop()
                                        .into(profileUserPic);
                                Glide.with(ProfileActivity.this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.round_bg)
                                        .centerCrop()
                                        .into(profileUserPicHeader);
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
            Glide.with(ProfileActivity.this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.round_bg) // Replace with your default image
                    .into(profileUserPic);
            Glide.with(ProfileActivity.this)
                    .load(profilePicUrl)
                    .placeholder(R.drawable.round_bg) // Replace with your default image
                    .into(profileUserPicHeader);
        } else {
            profileUserPic.setImageResource(R.drawable.round_bg); // Set default image
            profileUserPicHeader.setImageResource(R.drawable.round_bg); // Set default image
        }

        // Set the username and email
        profileUsername.setText(username != null ? username : "Username");
        profileUsernameHeader.setText(username != null ? username : "Username");
        profileEmail.setText(email != null ? email : "Email");
        profileEmailHeader.setText(email != null ? email : "Email");

        //Set the number of censored images
        numCensoredImgs.setText(String.valueOf(numCensoredImages));
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_profile1) {
            return true;
        }
        if (id == R.id.nav_home1) {
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.nav_settings1) {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.nav_about1) {
            Intent intent = new Intent(ProfileActivity.this, AboutUs.class);
            startActivity(intent);
            finish();
        }
        if (id == R.id.nav_feedback) {
            Intent intent = new Intent(ProfileActivity.this, FeedbackSurvey.class);
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