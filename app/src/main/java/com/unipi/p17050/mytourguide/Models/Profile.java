package com.unipi.p17050.mytourguide.Models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Profile {
   private ArrayList<String> interests= new ArrayList<String>();
   private ArrayList<Integer> accessibility=new ArrayList<>();

    public ArrayList<String> getInterests() {

        return interests;
    }

    public void setInterests(ArrayList<String> interests) {
        this.interests = interests;
    }

    public ArrayList<Integer> getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(ArrayList<Integer> accessibility) {
        this.accessibility = accessibility;
    }
    public void addInterest(String value){
        if(!interests.contains(value))
            interests.add(value);
    }
    public void removeInterest(String value){
            interests.remove(value);
    }

    public void addAccessibility(int value){
        if(!accessibility.contains(value))
            accessibility.add(value);
    }
    public void removeAccessibility(Object value){
        accessibility.remove(value);
    }
}
