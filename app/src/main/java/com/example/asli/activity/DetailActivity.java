package com.example.asli.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.asli.R;
import com.example.asli.model.Destination;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView imageView = findViewById(R.id.imageView);
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewCountry = findViewById(R.id.textViewCountry);
        TextView textViewContinent = findViewById(R.id.textViewContinent);
        TextView textViewDescription = findViewById(R.id.textViewDescription);
        TextView textViewPopulation = findViewById(R.id.textViewPopulation);
        TextView textViewCurrency = findViewById(R.id.textViewCurrency);
        TextView textViewLanguage = findViewById(R.id.textViewLanguage);
        TextView textViewActivities = findViewById(R.id.textViewActivities);

        Intent intent = getIntent();
        if (intent != null) {
            Destination destination = intent.getParcelableExtra("destination");
            if (destination != null) {
                textViewName.setText(destination.getName());
                textViewCountry.setText(destination.getCountry());
                textViewContinent.setText(destination.getContinent());
                textViewDescription.setText(destination.getDescription());
                textViewPopulation.setText(destination.getPopulation());
                textViewCurrency.setText(destination.getCurrency());
                textViewLanguage.setText(destination.getLanguage());
                textViewActivities.setText(formatListData(destination.getActivities()));
                Glide.with(this).load(destination.getImage()).into(imageView);
            } else {
                Toast.makeText(this, "No destination data found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Intent is null", Toast.LENGTH_SHORT).show();
        }
    }

    private String formatListData(ArrayList<String> dataList) {
        StringBuilder dataBuilder = new StringBuilder();
        if (dataList != null && !dataList.isEmpty()) {
            for (String data : dataList) {
                dataBuilder.append("• ").append(data).append("\n");
            }
        } else {
            dataBuilder.append("No data available");
        }
        return dataBuilder.toString();
    }
}


