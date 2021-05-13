package com.example.hunter;


import android.net.Uri;

import java.util.Date;
import java.util.List;

public class Posts {
    private String caption, postId;
    private Date date_created;
    private List<String> photo_id;
    private String user_id;
    private String place_id;
    private List<String> likes;
//    private List<String> comments;
    private Double rating,priceLvl;


    public Posts(){

    }

    public Posts(String postId,String caption, Date date_created, String user_id, String place_id, List<String> likes, Double rating,Double priceLvl) {
        this.postId=postId;
        this.caption = caption;
        this.date_created = date_created;
//        this.photo_id = photo_id;
        this.user_id = user_id;
        this.place_id = place_id;
        this.likes = likes;
//        this.comments = comments;
        this.rating=rating;
        this.priceLvl=priceLvl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public List<String> getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(List<String> photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

//    public List<String> getComments() {
//        return comments;
//    }
//
//    public void setComments(List<String> comments) {
//        this.comments = comments;
//    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Double getPriceLvl() {
        return priceLvl;
    }

    public void setPriceLvl(Double priceLvl) {
        this.priceLvl = priceLvl;
    }
}
