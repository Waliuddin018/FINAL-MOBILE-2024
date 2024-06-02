package com.example.asli.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.asli.R;
import com.example.asli.adapter.ContinentAdapter;
import com.example.asli.api.ApiService;
import com.example.asli.api.DestinationClient;
import com.example.asli.help.DBHelper;
import com.example.asli.model.Continent;
import com.example.asli.model.Destination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DestinationFragment extends Fragment {

    private RecyclerView recyclerView;
    private ContinentAdapter continentAdapter;
    private DBHelper dbHelper;
    private String loggedInUser;
    private LottieAnimationView progressBar;
    private TextView textViewConnectionLost;
    private Button btn_retry;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_destination, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressBar = view.findViewById(R.id.lottieProgressBar);
        textViewConnectionLost = view.findViewById(R.id.text_connection_lost);
        btn_retry = view.findViewById(R.id.btn_retry);

        dbHelper = new DBHelper(getContext());
        loggedInUser = dbHelper.getLoggedInUser();

        progressBar.setVisibility(View.VISIBLE);
        progressBar.playAnimation();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isNetworkAvailable(getContext())) {
                loadDestinations();
            } else {
                showConnectionLost();
            }
        }, 2000);

        ImageView imageViewProfile = view.findViewById(R.id.imageViewProfile);
        imageViewProfile.setOnClickListener(v -> goToProfileFragment());

        btn_retry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.playAnimation();
            btn_retry.setVisibility(View.GONE);
            textViewConnectionLost.setVisibility(View.GONE);

            executorService.execute(() -> {
                if (isNetworkAvailable(getContext())) {
                    getActivity().runOnUiThread(this::loadDestinations);
                } else {
                    getActivity().runOnUiThread(this::handleRetryFailure);
                }
            });
        });

        return view;
    }

    private void loadDestinations() {
        ApiService apiService = DestinationClient.getClient().create(ApiService.class);
        Call<List<Destination>> call = apiService.getDestinations();
        call.enqueue(new Callback<List<Destination>>() {
            @Override
            public void onResponse(Call<List<Destination>> call, Response<List<Destination>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    List<Destination> destinations = response.body();
                    if (destinations != null && !destinations.isEmpty()) {
                        List<Continent> continents = groupByContinent(destinations);
                        continentAdapter = new ContinentAdapter(getContext(), continents, dbHelper, loggedInUser);
                        recyclerView.setAdapter(continentAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to retrieve data: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Destination>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showConnectionLost() {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        textViewConnectionLost.setVisibility(View.VISIBLE);
        btn_retry.setVisibility(View.VISIBLE);
    }

    private void handleRetryFailure() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            btn_retry.setVisibility(View.VISIBLE);
            textViewConnectionLost.setVisibility(View.VISIBLE);
        }, 300);
    }

    private List<Continent> groupByContinent(List<Destination> destinations) {
        HashMap<String, List<Destination>> map = new HashMap<>();
        for (Destination destination : destinations) {
            if (!map.containsKey(destination.getContinent())) {
                map.put(destination.getContinent(), new ArrayList<>());
            }
            map.get(destination.getContinent()).add(destination);
        }
        List<Continent> continents = new ArrayList<>();
        for (String continent : map.keySet()) {
            continents.add(new Continent(continent, map.get(continent)));
        }
        return continents;
    }

    private void goToProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        requireActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, profileFragment)
                .addToBackStack(null)
                .commit();
    }
}
