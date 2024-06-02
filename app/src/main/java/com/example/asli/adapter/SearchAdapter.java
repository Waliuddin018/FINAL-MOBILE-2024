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
import com.example.asli.model.SearchResult;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private Context context;
    private List<SearchResult> searchResults;

    public SearchAdapter(Context context, List<SearchResult> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchResult searchResult = searchResults.get(position);
        holder.bind(searchResult);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewThumbnail;
        private TextView textViewName;
        private TextView textViewCountry;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewThumbnail = itemView.findViewById(R.id.iv_search);
            textViewName = itemView.findViewById(R.id.tv_username);
            textViewCountry = itemView.findViewById(R.id.tv_name);
        }

        public void bind(SearchResult searchResult) {
            textViewName.setText(searchResult.getName());
            textViewCountry.setText(searchResult.getCountry());

            Glide.with(itemView.getContext())
                    .load(searchResult.getImageUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(imageViewThumbnail);

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailActivity.class);
                Destination destination = new Destination(
                        searchResult.getId(),
                        searchResult.getName(),
                        searchResult.getCountry(),
                        searchResult.getImageUrl(),
                        searchResult.getContinent(),
                        searchResult.getDescription(),
                        searchResult.getPopulation(),
                        searchResult.getCurrency(),
                        searchResult.getLanguage(),
                        searchResult.getActivities()
                );
                intent.putExtra("destination", destination); // Kirim objek Destination
                context.startActivity(intent);
            });
        }
    }
}
