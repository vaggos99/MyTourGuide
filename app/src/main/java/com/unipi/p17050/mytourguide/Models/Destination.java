package com.unipi.p17050.mytourguide.Models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Destination {
    private String name;



    private String type;
    private ArrayList<String> category;
    private ArrayList<String> age_group;
    private int accessibility;
    private float latitude ;
    private float longitude ;

    public ArrayList<String> getAge_group() {
        return age_group;
    }

    public void setAge_group(ArrayList<String> age_group) {
        this.age_group = age_group;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getCategory() {
        return category;
    }

    public void setCategory(ArrayList<String> category) {
        this.category = category;
    }

    public int getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(int accessibility) {
        this.accessibility = accessibility;
    }


}
