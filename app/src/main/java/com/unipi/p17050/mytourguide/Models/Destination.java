package com.unipi.p17050.mytourguide.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Destination {
    private String type;
    private ArrayList<String> category;
    private int accessibility;
    private LatLng location;

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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
