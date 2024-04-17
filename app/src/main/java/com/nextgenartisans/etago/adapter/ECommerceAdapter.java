package com.nextgenartisans.etago.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.model.ECommercePlatform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ECommerceAdapter extends RecyclerView.Adapter<ECommerceAdapter.ViewHolder> {

    private List<ECommercePlatform> platforms;
    private Context context;

    public ECommerceAdapter(Context context, List<ECommercePlatform> platforms) {
        this.context = context;
        this.platforms = platforms;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.e_tago_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ECommercePlatform platform = platforms.get(position);
        holder.nameTextView.setText(platform.getName());
        holder.logoImageView.setImageResource(platform.getLogoDrawable());
        holder.itemView.setOnClickListener(v -> {
            // Open the app when the item is clicked
            openApp(context, platform.getPackageName());
        });
    }

    @Override
    public int getItemCount() {
        return platforms.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView logoImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            logoImageView = itemView.findViewById(R.id.logo);
        }
    }

    private void openApp(Context context, String packageName) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            // Get the SaveAndShareInstance object for the current user
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            DocumentReference docRef = db.collection("SaveAndShareInstances").document(user.getUid());

            // Increment the numShareInstance field
            Map<String, Object> updates = new HashMap<>();
            updates.put("numShareInstance", FieldValue.increment(1));
            updates.put("userID", user.getUid());
            updates.put("dateShared", FieldValue.serverTimestamp());

            // Increment the numShareInstance field
            docRef.set(updates, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot successfully updated!"))
                    .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));


            context.startActivity(launchIntent);
        } else {
            Toast.makeText(context, "App not installed.", Toast.LENGTH_SHORT).show();
        }
    }
}


