package com.example.asli.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResult implements Parcelable {
    public int getId() {
        return id;
    }

    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    @SerializedName("country")
    private String country;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("continent")
    private String continent;

    @SerializedName("description")
    private String description;

    @SerializedName("population")
    private String population;

    @SerializedName("currency")
    private String currency;

    @SerializedName("language")
    private String language;

    @SerializedName("best_time_to_visit")
    private String bestTimeToVisit;

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    @SerializedName("activities")
    private List<String> activities;

    public SearchResult(Integer id, String name, String country, String imageUrl, String continent, String description, String population, String currency, String language,List<String> activities) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.imageUrl = imageUrl;
        this.continent = continent;
        this.description = description;
        this.population = population;
        this.currency = currency;
        this.language = language;
        this.activities=activities;
    }

    public SearchResult(String name, String country, String image) {
        this.name = name;
        this.country = country;
        this.imageUrl = image;
    }

    protected SearchResult(Parcel in) {
        name = in.readString();
        country = in.readString();
        imageUrl = in.readString();
        continent = in.readString();
        description = in.readString();
        population = in.readString();
        currency = in.readString();
        language = in.readString();
        bestTimeToVisit = in.readString();
    }

    public static final Creator<SearchResult> CREATOR = new Creator<SearchResult>() {
        @Override
        public SearchResult createFromParcel(Parcel in) {
            return new SearchResult(in);
        }

        @Override
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getContinent() {
        return continent;
    }

    public String getDescription() {
        return description;
    }

    public String getPopulation() {
        return population;
    }

    public String getCurrency() {
        return currency;
    }

    public String getLanguage() {
        return language;
    }

    public String getBestTimeToVisit() {
        return bestTimeToVisit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(imageUrl);
        dest.writeString(continent);
        dest.writeString(description);
        dest.writeString(population);
        dest.writeString(currency);
        dest.writeString(language);
        dest.writeString(bestTimeToVisit);
    }



}
