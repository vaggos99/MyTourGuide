package com.unipi.p17050.mytourguide.Models;



import java.util.ArrayList;

public class Profile {
    private ArrayList<String> interests = new ArrayList<>();
    private ArrayList<String> favorites=new ArrayList<>();
    private String transport="Undefined";
    private String age_group="Undefined";
    private boolean pushchair;
    private boolean children;

    public ArrayList<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<String> favorites) {
        this.favorites = favorites;
    }

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
