package com.example.asli.api;

import com.example.asli.model.Destination;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;


public interface ApiService {
    @GET("destinations")
    Call<List<Destination>> getDestinations();
}

