package com.example.asli.model;

import java.util.List;

public class Continent {
    private String name;
    private List<Destination> destinations;

    public Continent(String name, List<Destination> destinations) {
        this.name = name;
        this.destinations = destinations;
    }

    public String getName() {
        return name;
    }

    public List<Destination> getDestinations() {
        return destinations;
    }
}
