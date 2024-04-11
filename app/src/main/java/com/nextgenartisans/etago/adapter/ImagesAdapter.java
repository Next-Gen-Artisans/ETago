package com.nextgenartisans.etago.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nextgenartisans.etago.R;
import com.nextgenartisans.etago.dialogs.OnMultipleImgClickDialog;

import java.util.ArrayList;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Uri> imagesList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Uri uri);
    }

    public ImagesAdapter(Context context, ArrayList<Uri> imagesList, OnItemClickListener listener) {
        this.context = context;
        this.imagesList = imagesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.multi_image_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri imageUri = imagesList.get(position);
        Glide.with(context).load(imageUri).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            OnMultipleImgClickDialog dialog = new OnMultipleImgClickDialog(context);
            dialog.setImage(imageUri);
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}

