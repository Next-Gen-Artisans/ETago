package com.nextgenartisans.etago;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.Manifest;



public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    ImageButton imageButton;

    FirebaseAuth auth;
    FirebaseUser user;

    // Get the navigation header view
    View headerView;

    // Find the TextViews by their IDs
    TextView usernameTextView;
    TextView emailTextView;
    ShapeableImageView userProfilePic;


    LogoutDialog logoutDialog;

    //MEDIA PERMISSIONS
    private static final int STORAGE_PERMISSION_CODE = 1;

    //


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

        //TODO CHECK FOR MEDIA PERMISSIONS
        //checkForPermissions();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //Custom Logout Dialog
        logoutDialog = new LogoutDialog(MainActivity.this);

        //Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.custom_toolbar);

        //TextViews
        headerView = navigationView.getHeaderView(0);
        userProfilePic = (ShapeableImageView) headerView.findViewById(R.id.drawer_user_profile_pic);
        usernameTextView = (TextView) headerView.findViewById(R.id.drawer_username);
        emailTextView = (TextView) headerView.findViewById(R.id.drawer_user_email);

        if(user == null){
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
        }else {
            // Set the username and email based on the Firebase user's information
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
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




    }

    private void checkForPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You have already granted this permission,", Toast.LENGTH_SHORT).show();

        } else {
            // Permissions have already been granted
            requestStoragePermissions();
            //loadPhotos();
        }
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Log.d(TAG, "Showing Media Permission Dialog");
            MediaPermissionDialog mediaPermissionDialog = new MediaPermissionDialog(this);
            mediaPermissionDialog.show();
            mediaPermissionDialog.mediaPermissionDialogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Prompt the user once explanation has been shown
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_CODE);

                    loadPhotos();
                }
            });
            mediaPermissionDialog.cancelMediaPermissionDialogBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPermissionDialog.dismiss();
                }
            });

        } else {
            Log.d(TAG, "Requesting Permission Directly");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
                // Permission granted. You can access photos here.
                loadPhotos();
            } else {
                // Permission denied.
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadPhotos() {
        // Implement logic to load and display photos from external storage
        Log.d(TAG, "Photos loaded.");
        //Toast.makeText(this, "Photos loaded.", Toast.LENGTH_SHORT).show();
        // Assuming photos are loaded successfully, update Firestore
        updateFirestoreUserAgreedMedia();
    }

    private void updateFirestoreUserAgreedMedia() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (user != null) {
            db.collection("Users").document(user.getUid())
                    .update("userAgreedMedia", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Successfully updated the document
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle the failure
                            Log.w(TAG, "Error updating document", e);
                        }
                    });
        } else {
            // Handle the case where the user is null
            Log.e(TAG, "User is null, cannot update Firestore");
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

    public void goToWelcome() {
        // Start the WelcomeActivity
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, Welcome.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.nav_home1){
            return true;
        }
        if(id == R.id.nav_profile1) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }
        if(id == R.id.nav_settings1) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);

        }
        if(id == R.id.nav_about1) {
            Intent intent = new Intent(MainActivity.this, AboutAppActivity.class);
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
            // Open the drawer if it's closed
            super.onBackPressed();
        }
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