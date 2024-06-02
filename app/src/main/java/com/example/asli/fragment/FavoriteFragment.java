package com.example.asli.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.example.asli.R;
import com.example.asli.adapter.FavoriteAdapter;
import com.example.asli.api.ApiService;
import com.example.asli.api.DestinationClient;
import com.example.asli.help.DBHelper;
import com.example.asli.model.Destination;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoriteAdapter favoriteAdapter;
    private DBHelper dbHelper;
    private String loggedInUser;
    private List<Destination> allDestinations = new ArrayList<>();
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
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        dbHelper = new DBHelper(getContext());
        loggedInUser = dbHelper.getLoggedInUser();

        recyclerView = view.findViewById(R.id.recyclerViewFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteAdapter = new FavoriteAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(favoriteAdapter);

        progressBar = view.findViewById(R.id.lottieProgressBar);
        textViewConnectionLost = view.findViewById(R.id.text_connection_lost);
        btn_retry = view.findViewById(R.id.btn_retry);

        loadDestinationsWithRetry();

        btn_retry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.playAnimation();
            btn_retry.setVisibility(View.GONE);
            textViewConnectionLost.setVisibility(View.GONE);

            executorService.execute(() -> {
                if (isNetworkAvailable(getContext())) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        loadDestinations();
                    });
                } else {
                    getActivity().runOnUiThread(this::handleRetryFailure);
                }
            });
        });

        return view;
    }

    private void loadDestinationsWithRetry() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.playAnimation();
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (isNetworkAvailable(getContext())) {
                loadDestinations();
            } else {
                showConnectionLost();
            }
        }, 2000);
    }

    private void loadDestinations() {
        ApiService apiService = DestinationClient.getClient().create(ApiService.class);
        Call<List<Destination>> call = apiService.getDestinations();
        call.enqueue(new Callback<List<Destination>>() {
            @Override
            public void onResponse(Call<List<Destination>> call, Response<List<Destination>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allDestinations = response.body();
                    filterFavoriteDestinations();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch destinations", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Destination>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
        }, 300); // Display progress bar for a brief moment
    }

    private void filterFavoriteDestinations() {
        List<Destination> favoriteDestinations = new ArrayList<>();
        for (Destination destination : allDestinations) {
            if (dbHelper.isFavorite(loggedInUser, String.valueOf(destination.getId()))) {
                favoriteDestinations.add(destination);
            }
        }
        if (!favoriteDestinations.isEmpty()) {
            favoriteAdapter.updateData(favoriteDestinations);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getContext(), "No favorite destinations found", Toast.LENGTH_SHORT).show();
        }
    }
}
