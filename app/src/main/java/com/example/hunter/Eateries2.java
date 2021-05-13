package com.example.hunter;

import java.util.List;

public class Eateries2 {

    private String address,placeID,placeName;
    private List<String> category;
    private Double rating,price_level;

    public Eateries2(){

    }


    public Eateries2(String address, List<String> category, String placeID, String placeName, Double rating,Double price_level) {
        this.address = address;
        this.category = category;
        this.placeID = placeID;
        this.placeName = placeName;
        this.rating=rating;
        this.price_level=price_level;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getPrice_level() {
        return price_level;
    }

    public void setPrice_level(Double price_level) {
        this.price_level = price_level;
    }
}
