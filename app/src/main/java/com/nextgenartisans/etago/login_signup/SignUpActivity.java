package com.nextgenartisans.etago.login_signup;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.dialogs.CustomSignInDialog;
import com.nextgenartisans.etago.dialogs.TermsOfServiceDialog;
import com.nextgenartisans.etago.home.MainActivity;
import com.nextgenartisans.etago.model.Users;
import com.nextgenartisans.etago.onboarding.Welcome;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SignUpActivity extends AppCompatActivity {


    LinearLayout signupHeader, signupFooter, signupOptions;
    ImageButton signupBackBtn, googleSignupBtn;
    CardView signupForm;
    TextInputLayout signupUsernameInput, signupEmailInput, signupPassInput;
    TextInputEditText userSignupUsername, userSignupEmail, userSignupPass;
    AppCompatButton signUpBtn;
    TextView textLogIn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager mCallbackManager;
    private CustomSignInDialog customSignInDialog;
    private TermsOfServiceDialog termsOfServiceDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Change status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }

        //Set Content View
        setContentView(R.layout.activity_sign_up);

        //Set id for clickable text and buttons
        signUpBtn = findViewById(R.id.signup_btn);
        textLogIn = findViewById(R.id.text_log_in);
        googleSignupBtn = findViewById(R.id.google_signup_btn);
        signupBackBtn = findViewById(R.id.signup_back_btn);

        //Set id for non-interactive components
        signupHeader = findViewById(R.id.signup_header);
        signupFooter = findViewById(R.id.signup_footer);
        signupOptions = findViewById(R.id.signup_options);
        signupForm = findViewById(R.id.signup_form);


        //FIREBASE INSTANCE
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Set id for input text
        signupUsernameInput = findViewById(R.id.signup_username_input);
        signupEmailInput = findViewById(R.id.signup_email_input);
        signupPassInput = findViewById(R.id.signup_pass_input);
        userSignupUsername = findViewById(R.id.user_signup_username);
        userSignupEmail = findViewById(R.id.user_signup_email);
        userSignupPass = findViewById(R.id.user_signup_pass);


        //Remove animation when input text is focused by user
        userSignupUsername.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                signupUsernameInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (userSignupUsername.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    signupUsernameInput.setHint("Username");
                }
            }
        });

        userSignupEmail.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                signupEmailInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (userSignupEmail.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    signupEmailInput.setHint("Email");
                }
            }
        });

        userSignupPass.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                // When the input field is selected (has focus), clear the hint text
                signupPassInput.setHint("");
            } else {
                // When the input field loses focus, check if it has content
                if (userSignupPass.getText().toString().isEmpty()) {
                    // Restore the hint text only if the input is empty
                    signupPassInput.setHint("Password");
                }
            }
        });


        textLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        signupBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignUpActivity.this, Welcome.class);
                startActivity(i);
                finish();
            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = userSignupUsername.getText().toString();
                String email = userSignupEmail.getText().toString();
                String password = userSignupPass.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password.", Toast.LENGTH_SHORT).show();
                    return;
                }

                customSignInDialog.setMessage("Creating account...");
                customSignInDialog.showAuthProgress(true);
                customSignInDialog.setProceedButtonVisible(false);
                customSignInDialog.show();

                // Create user with Firebase Auth
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    if (task.getException() != null) {
                                        Log.e(TAG, "Failed to create account: " + task.getException().getMessage());
                                        // Show more descriptive error based on the exception
                                        customSignInDialog.setMessage(task.getException().getMessage());
                                        customSignInDialog.setProceedButtonVisible(true);
                                    } else {
                                        Log.e(TAG, "Failed to create account for an unknown reason.");
                                        customSignInDialog.setMessage("Failed to create account for an unknown reason.");
                                        customSignInDialog.setProceedButtonVisible(true);
                                    }
                                    customSignInDialog.showAuthFailedProgress(false);
                                } else {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    createUserInFirestoreManual(user, username);
                                }
                            }
                        });

            }
        });

        // Initialize the custom progress dialog
        customSignInDialog = new CustomSignInDialog(this);

        // Setting the listener for the proceed button in the dialog
        customSignInDialog.setProceedButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customSignInDialog.dismiss(); // Dismiss the dialog
                promptLogin(); // Call updateUI when the proceed button is clicked

            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        googleSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

    }

    private void createUserInFirestoreManual(FirebaseUser firebaseUser, String username) {
        // Reference to your Firebase Storage file
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_username_icon.png");

        // Get the download URL
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for the default profile picture
                String defaultProfilePicUrl = uri.toString();


                // Prepare user data with the default profile picture URL
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("email", firebaseUser.getEmail());
                userMap.put("numCensoredImgs", 0);
                userMap.put("profilePic", firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : defaultProfilePicUrl);
                userMap.put("userAgreedMedia", false);
                userMap.put("userAgreedTermsAndPrivacyPolicy", false);
                userMap.put("userPasswordSet", true);
                userMap.put("userID", firebaseUser.getUid());
                userMap.put("username", username);
                userMap.put("apiCallsLimit", 50); // Set the initial API calls limit here
                userMap.put("accountCreationTimestamp", FieldValue.serverTimestamp());

                // Store in Firestore
                db.collection("Users").document(firebaseUser.getUid())
                        .set(userMap)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                customSignInDialog.setMessage("Account created successfully.");
                                customSignInDialog.showAuthProgress(false);
                                customSignInDialog.setProceedButtonVisible(true);
                                // Redirect to login or main activity as required
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                customSignInDialog.setMessage("Failed to create account.");
                                customSignInDialog.showAuthFailedProgress(false);
                            }
                        });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }


    int RC_SIGN_IN = 40;

    private void googleSignIn() {
        mGoogleSignInClient.signOut();
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);

                // Show the custom dialog with progress
                customSignInDialog.setMessage("Creating account...");
                customSignInDialog.showAuthProgress(true);
                customSignInDialog.setProceedButtonVisible(false);
                customSignInDialog.show();

                firebaseAuth(account.getIdToken());

            } catch (ApiException e) {
                // Handle error
                // Update dialog to show error message
                customSignInDialog.setMessage("Creating account failed.");
                customSignInDialog.showAuthFailedProgress(false);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            if (firebaseUser != null) {
                                // Check if user already exists in Firestore
                                checkUserInFirestore(firebaseUser);

                            } else {
                                // Authentication failed
                                customSignInDialog.setMessage("Creating account failed.");
                                customSignInDialog.showAuthFailedProgress(false);
                            }
                        } else {
                            Log.d(TAG, "Authenticating user failed.");
                        }
                    }
                });
    }

    private void checkUserInFirestore(FirebaseUser firebaseUser) {
        DocumentReference docRef = db.collection("Users").document(firebaseUser.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // User already exists in Firestore, just log in
                        Log.d(TAG, "User already exists in Firestore.");

                        // Redirect to main activity or update UI
                        // Authentication success
                        customSignInDialog.setMessage("Account already exists. Proceed to log in.");
                        customSignInDialog.showAuthProgress(false); // Show check icon
                        customSignInDialog.setProceedButtonVisible(true); // Show proceed button


                    } else {
                        // User does not exist, create a new user document
                        customSignInDialog.setMessage("Account created successfully.");
                        customSignInDialog.showAuthProgress(false);
                        customSignInDialog.setProceedButtonVisible(true);
                        // Redirect to login or main activity as required
                        createUserInFirestore(firebaseUser);
                    }
                } else {
                    Log.d(TAG, "Checking user in firestore failed.");
                }
            }
        });
    }

    private void createUserInFirestore(FirebaseUser firebaseUser) {
        Users users = new Users();
        users.setUserID(firebaseUser.getUid());
        users.setUsername(firebaseUser.getDisplayName());
        users.setEmail(firebaseUser.getEmail());
        users.setApiCallsLimit(50); // Set the initial API calls limit here
        users.setAccountCreationTimestamp(FieldValue.serverTimestamp());

        // Download the profile picture from the URL provided by Google
        if (firebaseUser.getPhotoUrl() != null) {
            String googleProfilePicUrl = firebaseUser.getPhotoUrl().toString();
            new DownloadImageTask(bitmap -> {
                // Upload the downloaded image to Firebase Storage
                uploadImageToFirebaseStorage(bitmap, firebaseUser.getUid(), users);
            }).execute(googleProfilePicUrl);
        } else {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_username_icon.png");
            // If the user doesn't have a profile picture on Google, you can set a default one
            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String defaultProfilePicUrl = uri.toString();
                    users.setProfilePic(defaultProfilePicUrl);
                    saveUserToFirestore(users);
                }
            });


        }
    }

    private void uploadImageToFirebaseStorage(Bitmap bitmap, String userId, Users users) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicRef = storageRef.child("profilePics/" + userId + ".jpg");

        UploadTask uploadTask = profilePicRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Get the download URL of the uploaded image
            profilePicRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String firebaseProfilePicUrl = uri.toString();
                users.setProfilePic(firebaseProfilePicUrl);
                saveUserToFirestore(users);
            });
        }).addOnFailureListener(e -> {
            // Handle the error
        });
    }

    private void saveUserToFirestore(Users users) {
        db.collection("Users").document(users.getUserID())
                .set(users)
                .addOnSuccessListener(aVoid -> {
                    // Successfully added new user
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                    Log.w(TAG, "Error writing document", e);
                });
    }

    private static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private final Consumer<Bitmap> consumer;

        public DownloadImageTask(Consumer<Bitmap> consumer) {
            this.consumer = consumer;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            consumer.accept(result);
        }
    }

    private void updateUI() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    private void promptLogin() {
        mGoogleSignInClient.signOut();
        mAuth.signOut();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();
    }


}