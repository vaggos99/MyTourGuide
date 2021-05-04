package com.unipi.p17050.mytourguide.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Profile {
    private ArrayList<String> interests = new ArrayList<String>();
    private String transport="Undefined";
    private String age_group="Undefined";
    private boolean pushchair;
    private boolean children;


    public boolean isPushchair() {
        return pushchair;
    }

    public void setPushchair(boolean pushchair) {
        this.pushchair = pushchair;
    }

    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

    public String getTransport() {


        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getAge_group() {
        return age_group;
    }

    public void setAge_group(String age_group) {
        this.age_group = age_group;
    }


    public ArrayList<String> getInterests() {

        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }



    public void addInterest(String value) {
        if (!interests.contains(value))
            interests.add(value);
    }

    public void removeInterest(String value) {
        interests.remove(value);
    }


}
