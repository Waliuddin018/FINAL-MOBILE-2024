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
import com.example.asli.help.DBHelper;
import com.example.asli.R;
import com.example.asli.activity.DetailActivity;
import com.example.asli.activity.MainActivity;
import com.example.asli.model.Destination;
import com.squareup.picasso.Picasso;
import java.util.List;

public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    private List<Destination> destinations;
    private Context context;
    private DBHelper dbHelper;

    public DestinationAdapter(List<Destination> destinations, Context context) {
        this.destinations = destinations;
        this.context = context;
        dbHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Destination destination = destinations.get(position);
        holder.nameTextView.setText(destination.getName());
        Glide.with(context).load(destination.getImage()).into(holder.imageView);

        final boolean[] isFavorite = {dbHelper.isFavorite(dbHelper.getLoggedInUser(), String.valueOf(destination.getId()))};
        holder.favoriteImageView.setImageResource(isFavorite[0] ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);


        holder.favoriteImageView.setOnClickListener(v -> {
            if (isFavorite[0]) {
                dbHelper.removeFavorite(dbHelper.getLoggedInUser(),String.valueOf(destination.getId()));
                holder.favoriteImageView.setImageResource(R.drawable.ic_favorite_border);
            } else {
                dbHelper.addFavorite(dbHelper.getLoggedInUser(),String.valueOf(destination.getId()));
                holder.favoriteImageView.setImageResource(R.drawable.ic_favorite);
            }

            isFavorite[0] = !isFavorite[0];
            notifyItemChanged(position);
            // Notify the favorite fragment to update its data
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateFavoriteFragment();
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("destination", destination);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return destinations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView imageView;
        public ImageView favoriteImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            imageView = itemView.findViewById(R.id.imageView);
            favoriteImageView = itemView.findViewById(R.id.imageViewFavorite);
        }
    }
}
