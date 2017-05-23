package com.example.chenyijie.customframe.bean;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by chenyijie on 2017/5/18.
 */

public class ViewInfo {
    @Getter @Setter @SerializedName("Px") private String Longitude;
    @Getter @Setter @SerializedName("Py") private String Latitude;
    @Getter @Setter @SerializedName("Add") private String Add;
    @Getter @Setter @SerializedName("Name") private String name;
    // 以下是json沒有，隨機產生的資料
    @Getter @Setter private float rating;
    @Getter @Setter private int cost;
    @Getter @Setter private int comment;
    @Getter @Setter private String category;
    // 以下是透過經緯度來計算的真實距離
    @Getter @Setter private int distance;
    // 以下是用來記錄收藏和最愛的
    @Setter private boolean favorite;
    @Setter private boolean booked;

    public ViewInfo(){}

    public ViewInfo(String latitude, String longitude, String address
            , String name, float rating, int cost, int comment, String category
            , int distance, boolean booked, boolean favorited){
        this.Latitude = latitude;
        this.Longitude = longitude;
        this.Add = address;
        this.name = name;
        this.rating = rating;
        this.cost = cost;
        this.comment = comment;
        this.category = category;
        this.distance = distance;
        this.booked = booked;
        this.favorite = favorited;
    }

    public boolean getFavorite(){
        return this.favorite;
    }

    public boolean getBooked(){
        return this.booked;
    }
}
