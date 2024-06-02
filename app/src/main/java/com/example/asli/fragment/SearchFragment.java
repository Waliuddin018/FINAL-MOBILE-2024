package com.example.asli.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.asli.R;
import com.example.asli.adapter.SearchAdapter;
import com.example.asli.api.ApiService;
import com.example.asli.api.DestinationClient;
import com.example.asli.model.Destination;
import com.example.asli.model.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment {

    private RecyclerView recyclerViewSearchResults;
    private SearchAdapter searchAdapter;
    private List<SearchResult> searchResults;
    private ApiService apiService;
    private LottieAnimationView lottieAnimationView;
    private TextView textViewConnectionLost;

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        recyclerViewSearchResults = view.findViewById(R.id.recyclerViewSearchResults);
        textViewConnectionLost = view.findViewById(R.id.tvConnectionLost1);

        searchResults = new ArrayList<>();
        searchAdapter = new SearchAdapter(getContext(), searchResults);

        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewSearchResults.setAdapter(searchAdapter);

        apiService = DestinationClient.getClient().create(ApiService.class);

        SearchView searchView = view.findViewById(R.id.Search);
        searchView.setQueryHint("search destination");
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                lottieAnimationView.setVisibility(View.VISIBLE);
                lottieAnimationView.playAnimation();
                recyclerViewSearchResults.setVisibility(View.GONE);
                textViewConnectionLost.setVisibility(View.GONE);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    if (!TextUtils.isEmpty(newText)) {
                        performSearch(newText);
                    } else {
                        clearSearchResults();
                    }
                });
                return true;
            }
        });
        return view;
    }

    private void performSearch(String query) {
        if (!isNetworkAvailable(getContext())) {
            showNoInternetMessage();
        } else {
            Call<List<Destination>> call = apiService.getDestinations();
            call.enqueue(new Callback<List<Destination>>() {
                @Override
                public void onResponse(Call<List<Destination>> call, Response<List<Destination>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Destination> destinations = response.body();
                        searchResults.clear();
                        for (Destination destination : destinations) {
                            if (destination.getName().toLowerCase().startsWith(query.toLowerCase())) {
                                SearchResult searchResult = new SearchResult(
                                        destination.getId(),
                                        destination.getName(),
                                        destination.getCountry(),
                                        destination.getImage(),
                                        destination.getContinent(),
                                        destination.getDescription(),
                                        destination.getPopulation(),
                                        destination.getCurrency(),
                                        destination.getLanguage(),
                                        destination.getActivities()
                                );
                                searchResults.add(searchResult);
                            }
                        }
                        updateSearchResults();
                    } else {
                        showToast("Failed to search data");
                    }
                }

                @Override
                public void onFailure(Call<List<Destination>> call, Throwable t) {
                    showToast("Error: " + t.getMessage());
                }
            });
        }
    }

    private void clearSearchResults() {
        getActivity().runOnUiThread(() -> {
            searchResults.clear();
            searchAdapter.notifyDataSetChanged();
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
            recyclerViewSearchResults.setVisibility(View.VISIBLE);
        });
    }

    private void updateSearchResults() {
        getActivity().runOnUiThread(() -> {
            searchAdapter.notifyDataSetChanged();
            if (searchResults.isEmpty()) {
                recyclerViewSearchResults.setVisibility(View.GONE);
            } else {
                recyclerViewSearchResults.setVisibility(View.VISIBLE);
            }
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
        });
    }

    private void showNoInternetMessage() {
        getActivity().runOnUiThread(() -> {
            textViewConnectionLost.setVisibility(View.VISIBLE);
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
        });
    }

    private void showToast(String message) {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            lottieAnimationView.setVisibility(View.GONE);
            lottieAnimationView.pauseAnimation();
        });
    }
}




