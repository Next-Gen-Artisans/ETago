package com.nextgenartisans.etago.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.model.ECommercePlatform;

import java.util.List;

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
            context.startActivity(launchIntent);
        } else {
            Toast.makeText(context, "App not installed.", Toast.LENGTH_SHORT).show();
        }
    }
}


