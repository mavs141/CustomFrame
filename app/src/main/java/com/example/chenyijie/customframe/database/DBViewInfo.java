package com.example.chenyijie.customframe.database;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 不能用lombok來做getter setter，所以只能自己來....
 *
 * Created by chenyijie on 2017/5/21.
 */

public class DBViewInfo extends RealmObject {
    @PrimaryKey
    private int id;
    private String longitude;
    private String latitude;
    private String address;
    private String date;
    private String name;
    private float rating;
    private int cost;
    private int comment;
    private String category;
    private boolean booked;
    private boolean favorited;

    public DBViewInfo(){}

    public DBViewInfo(String latitude, String longitude, String address
            , String name, float rating, int cost, int comment, String category
            , boolean booked, boolean favorited){
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.name = name;
        this.rating = rating;
        this.cost = cost;
        this.comment = comment;
        this.category = category;
        this.booked = booked;
        this.favorited = favorited;
    }

    public void setId(int id){
        this.id = id;
    }

    public int getId(){
        return this.id;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getDate(){
        return this.date;
    }

    public void setLongitude(String longitude){
        this.longitude = longitude;
    }

    public String getLongitude(){
        return longitude;
    }


    public void setLatitude(String latitude){
        this.latitude = latitude;
    }

    public String getLatitude(){
        return latitude;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setRating(float rating){
        this.rating = rating;
    }

    public float getRating(){
        return rating;
    }

    public void setCost(int cost){
        this.cost = cost;
    }

    public int getCost(){
        return cost;
    }

    public void setComment(int comment){
        this.comment = comment;
    }

    public int getComment(){
        return comment;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public String getCategory(){
        return category;
    }

    public void setBooked(boolean booked){
        this.booked = booked;
    }

    public boolean getBooked(){
        return booked;
    }

    public void setFavorited(boolean favorited){
        this.favorited = favorited;
    }

    public boolean getFavorited(){
        return favorited;
    }
}
