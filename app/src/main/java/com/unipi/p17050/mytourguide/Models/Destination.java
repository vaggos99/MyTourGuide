package com.unipi.p17050.mytourguide.Models;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Destination {
    private String name;

    private String name_gr;

    private String image;
    private String info;
    private String info_gr;
    private boolean easy_access;
    private String type;
    private ArrayList<String> category;
    private ArrayList<String> age_group;
    private int accessibility;
    private My_Location location;
    private String type_gr;

    public String getInfo_gr() {
        return info_gr;
    }

    public void setInfo_gr(String info_gr) {
        this.info_gr = info_gr;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isEasy_access() {
        return easy_access;
    }

    public void setEasy_access(boolean easy_access) {
        this.easy_access = easy_access;
    }

    public String getType_gr() {
        return type_gr;
    }

    public void setType_gr(String type_gr) {
        this.type_gr = type_gr;
    }

    public My_Location getLocation() {
        return location;
    }

    public void setLocation(My_Location location) {
        this.location = location;
    }

    public String getName_gr() {
        return name_gr;
    }

    public void setName_gr(String name_gr) {
        this.name_gr = name_gr;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
