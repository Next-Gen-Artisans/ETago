package com.nextgenartisans.etago.dialogs;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nextgenartisans.etago.R;

public class MediaPermissionDialog extends Dialog {

    LinearLayout mediaPermissionDialogButtons;
    AppCompatButton mediaPermissionDialogBtn, cancelMediaPermissionDialogBtn;
    TextView mediaPermissionDialogTitle, mediaPermissionDialogText;


    private int storagePermissionCode;

    public interface OnPermissionGrantedListener {
        void onPermissionGranted();
    }

    public void setOnPermissionGrantedListener(OnPermissionGrantedListener listener) {
        this.listener = listener;
    }

    private OnPermissionGrantedListener listener;

    public MediaPermissionDialog(@NonNull Context context) {
        super(context);
        this.storagePermissionCode = storagePermissionCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_media_permission_dialog);

        // Set the background of the dialog window to transparent
        Window window = getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        mediaPermissionDialogTitle = findViewById(R.id.media_permission_dialog_title);
        mediaPermissionDialogText = findViewById(R.id.media_permission_dialog_text);
        mediaPermissionDialogButtons = findViewById(R.id.media_permission_dialog_buttons);
        mediaPermissionDialogBtn = findViewById(R.id.media_permission_dialog_btn);

        mediaPermissionDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update the Firestore document for the user to indicate they have agreed
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    DocumentReference userDocRef = db.collection("Users").document(currentUser.getUid());
                    userDocRef.update("userAgreedMedia", true)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // Successfully updated the user's agreement status
                                    dismiss();
                                    // Proceed with the main activity or other logic after the user has agreed
                                    Toast.makeText(getContext(), "You have agreed to allow E-Tago to access camera and media files.", Toast.LENGTH_LONG).show();
                                    Log.d(TAG,"USER AGREED ON DIALOG");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "You have not agreed to allow E-Tago to access camera and media files.", Toast.LENGTH_LONG).show();
                                    Log.d(TAG,"USER DID NOT AGREE ON DIALOG");
                                }
                            });
                }
            }
        });


    }

}
