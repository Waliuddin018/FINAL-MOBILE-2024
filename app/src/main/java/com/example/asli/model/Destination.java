package com.example.asli.model;

import static java.lang.System.in;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Destination implements Parcelable {
    private int id;
    private String name;
    private String country;

    private String continent;
    private String description;
    private String image;
    private String population;
    private String currency;
    private String language;
    private String bestTimeToVisit;
    private List<String> topAttractions;
    private List<String> localDishes;
    private List<String> activities;
    private boolean isFavorite;

    // Constructor, Getter, Setter

    public Destination(Integer id, String name, String country, String imageUrl, String continent, String description, String population, String currency, String language, List<String> activities) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.image = imageUrl;
        this.continent = continent;
        this.description = description;
        this.population = population;
        this.currency = currency;
        this.language = language;
        this.activities = activities;
    }



    protected Destination(Parcel in) {
        id = in.readInt();
        name = in.readString();
        country = in.readString();
        continent = in.readString();
        description = in.readString();
        image = in.readString();
        population = in.readString();
        currency = in.readString();
        language = in.readString();
        bestTimeToVisit = in.readString();
        topAttractions = in.createStringArrayList();
        localDishes = in.createStringArrayList();
        activities = in.createStringArrayList();
        isFavorite = in.readByte() != 0;
    }

    public Destination(int id, String name , String image) {
        this.id=id;
        this.name=name;
        this.image=image;

    }


    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Destination> CREATOR = new Creator<Destination>() {
        @Override
        public Destination createFromParcel(Parcel in) {
            return new Destination(in);
        }

        @Override
        public Destination[] newArray(int size) {
            return new Destination[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(country);
        dest.writeString(continent);
        dest.writeString(description);
        dest.writeString(image);
        dest.writeString(population);
        dest.writeString(currency);
        dest.writeString(language);
        dest.writeString(bestTimeToVisit);
        dest.writeStringList(topAttractions);
        dest.writeStringList(localDishes);
        dest.writeStringList(activities);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }


    // Getter and Setter methods

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBestTimeToVisit() {
        return bestTimeToVisit;
    }

    public void setBestTimeToVisit(String bestTimeToVisit) {
        this.bestTimeToVisit = bestTimeToVisit;
    }

    public ArrayList<String> getTopAttractions() {
        return (ArrayList<String>) topAttractions;
    }

    public void setTopAttractions(List<String> topAttractions) {
        this.topAttractions = topAttractions;
    }

    public ArrayList<String> getLocalDishes() {
        return (ArrayList<String>) localDishes;
    }

    public void setLocalDishes(List<String> localDishes) {
        this.localDishes = localDishes;
    }

    public ArrayList<String> getActivities() {
        return (ArrayList<String>) activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
