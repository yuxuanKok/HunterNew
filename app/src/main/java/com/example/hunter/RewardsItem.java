
package com.example.hunter;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class RewardsItem implements Parcelable {
    private String images;
    private String title;
    private String desc;
    private String rewardsID;
    private int points;
    private int quantitySold;
    private String terms;
    private String placeID;
    private String placeName;
    private String category;
    private Date availableUntil;

    public RewardsItem() {

    }

    public RewardsItem(String images, String title, String desc, String rewardsID, int points, int quantitySold, String terms, String placeID, String placeName, String category, Date availableUntil) {
        this.images = images;
        this.title = title;
        this.desc = desc;
        this.rewardsID = rewardsID;
        this.points = points;
        this.quantitySold = quantitySold;
        this.terms = terms;
        this.placeID = placeID;
        this.placeName = placeName;
        this.category = category;
        this.availableUntil = availableUntil;
    }

    protected RewardsItem(Parcel in) {
        images = in.readString();
        title = in.readString();
        desc = in.readString();
        rewardsID = in.readString();
        points = in.readInt();
        quantitySold = in.readInt();
        terms = in.readString();
        placeID = in.readString();
        placeName = in.readString();
        category = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(images);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeString(rewardsID);
        dest.writeInt(points);
        dest.writeInt(quantitySold);
        dest.writeString(terms);
        dest.writeString(placeID);
        dest.writeString(placeName);
        dest.writeString(category);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RewardsItem> CREATOR = new Creator<RewardsItem>() {
        @Override
        public RewardsItem createFromParcel(Parcel in) {
            return new RewardsItem(in);
        }

        @Override
        public RewardsItem[] newArray(int size) {
            return new RewardsItem[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRewardsID() {
        return rewardsID;
    }

    public void setRewardsID(String rewardsID) {
        this.rewardsID = rewardsID;
    }

    public int getPoints() {
        return Integer.valueOf(points);
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold = quantitySold;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getAvailableUntil() {
        return availableUntil;
    }

    public void setAvailableUntil(Date availableUntil) {
        this.availableUntil = availableUntil;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
}