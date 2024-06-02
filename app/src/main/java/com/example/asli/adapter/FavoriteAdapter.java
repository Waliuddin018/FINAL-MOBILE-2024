package com.example.asli.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.asli.R;
import com.example.asli.activity.DetailActivity;
import com.example.asli.model.Destination;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<Destination> favoriteDestinations;
    private Context context;

    public FavoriteAdapter(Context context, List<Destination> favoriteDestinations) {
        this.context = context;
        this.favoriteDestinations = favoriteDestinations;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Destination destination = favoriteDestinations.get(position);
        holder.nameTextView.setText(destination.getName());
        holder.descTextView.setText(destination.getDescription());
        Glide.with(context).load(destination.getImage()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("destination", destination);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoriteDestinations.size();
    }

    public void updateData(List<Destination> newFavorites) {
        this.favoriteDestinations = newFavorites;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView,descTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            descTextView= itemView.findViewById(R.id.TextViewDesc);
        }
    }
}
