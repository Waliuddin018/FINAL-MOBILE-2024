package com.example.asli.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.asli.help.DBHelper;
import com.example.asli.R;
import com.example.asli.model.Continent;

import java.util.List;

public class ContinentAdapter extends RecyclerView.Adapter<ContinentAdapter.ContinentViewHolder> {

    private Context context;
    private List<Continent> continents;
    private DBHelper dbHelper;
    private String loggedInUser;

    public ContinentAdapter(Context context, List<Continent> continents, DBHelper dbHelper, String loggedInUser) {
        this.context = context;
        this.continents = continents;
        this.dbHelper = dbHelper;
        this.loggedInUser = loggedInUser;
    }

    @NonNull
    @Override
    public ContinentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_continent, parent, false);
        return new ContinentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContinentViewHolder holder, int position) {
        Continent continent = continents.get(position);
        holder.textViewContinentName.setText(continent.getName());

        DestinationAdapter destinationAdapter = new DestinationAdapter(continent.getDestinations(), context);
        holder.recyclerViewDestinations.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerViewDestinations.setAdapter(destinationAdapter);
    }

    @Override
    public int getItemCount() {
        return continents.size();
    }

    public static class ContinentViewHolder extends RecyclerView.ViewHolder {
        TextView textViewContinentName;
        RecyclerView recyclerViewDestinations;

        public ContinentViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewContinentName = itemView.findViewById(R.id.continentTextView);
            recyclerViewDestinations = itemView.findViewById(R.id.destinationRecyclerView);
        }
    }
}
