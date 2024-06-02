package com.example.asli.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class DestinationClient {
    private static final String BASE_URL = "https://freetestapi.com/api/v1/";
    private static Retrofit retrofit = null;
    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
