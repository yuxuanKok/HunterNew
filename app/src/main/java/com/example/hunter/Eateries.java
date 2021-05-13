package com.example.hunter;

import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;

import java.util.ArrayList;
import java.util.List;

public class Eateries {

    private String address,phoneNum,phoneNum2,placeID,placeName;
    private Double price_level, postNum, totalPrice;
    private Double rating;
    private List<PhotoMetadata> photo;
    private List<String> category;
    private OpeningHours openingHours;
//    private List<Posts> posts;
    private Double totalRate;

    public Eateries(){

    }

    public Eateries(String address, List<String> category, OpeningHours openingHours, String phoneNum, List<PhotoMetadata> photo, String placeID, String placeName, Double price_level, Double rating,Double totalPrice,Double totalRate) {
        this.address = address;
        this.category = category;
        this.openingHours = openingHours;
        this.phoneNum = phoneNum;
        this.photo = photo;
        this.placeID = placeID;
        this.placeName = placeName;
        this.price_level = price_level;
        this.rating = rating;
//        this.posts = posts;
        this.totalRate=totalRate;
        this.totalPrice=totalPrice;
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


    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(OpeningHours openingHours) {
        this.openingHours = openingHours;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getPhoneNum2() {
        return phoneNum2;
    }

    public void setPhoneNum2(String phoneNum2) {
        this.phoneNum2 = phoneNum2;
    }

    public List<PhotoMetadata> getPhoto() {
        return photo;
    }

    public void setPhoto(List<PhotoMetadata> photo) {
        this.photo = photo;
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

    public Double getPrice_level() {
        return price_level;
    }

    public void setPrice_level(Double price_level) {
        this.price_level = price_level;
    }

    public Double getPostNum() {
        return postNum;
    }

    public void setPostNum(Double postNum) {
        this.postNum = postNum;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getTotalRate() {
        return totalRate;
    }

    public void setTotalRate(Double totalRate) {
        this.totalRate = totalRate;
    }
}
