package com.example.crapple_maps;

public class Favorites {
    private String name;
    private String address;
    private String rating;

    public Favorites(String name, String address, String rating) {
        this.name = name;
        this.address = address;
        this.rating = rating;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getRating() {
        return rating;
    }
}
