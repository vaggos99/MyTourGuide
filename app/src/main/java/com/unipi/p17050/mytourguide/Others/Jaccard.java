package com.unipi.p17050.mytourguide.Others;

import android.util.Log;

import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.Profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Jaccard {
    public static double calculate(Profile profile, Destination destination) {

        ArrayList profile_interests = new ArrayList(profile.getInterests());
        ArrayList destination_interests = new ArrayList(destination.getCategory());
        ArrayList transport_list = new ArrayList(destination.getTransport());
        double union = getUnionOfLists(profile_interests, destination_interests);
        double  intersect =  1.5*getIntersectOfLists(profile_interests, destination_interests) ;


        if (transport_list.contains(profile.getTransport()) && !profile.getTransport().equals("Undefined")) {
            intersect++;
            union ++;
        } else if (!profile.getTransport().equals("Undefined"))
            union ++;
        else{
            union+=transport_list.size();
            intersect+=transport_list.size();
        }

        if ((profile.getAge_group().equals("Elder") || profile.isPushchair())) {
            if (destination.isEasy_access())
                intersect++;
            union++;
        }

        if (profile.isChildren()) {
            if (destination.isChildren())
                intersect++;
            union++;
        }

        if(profile.getAge_group().equals("Teen/Adult") && !profile.isChildren()){
            if(destination.isNature()) {
                intersect++;
                union++;
            }

        }
        Log.d("Jaccard intersect",String.valueOf(intersect ));
        Log.d("Jaccard union",String.valueOf(union ));
        Log.d("Jaccard score",String.valueOf(intersect / union));
        return intersect / union;
    }


    private static double getUnionOfLists(List<String> list1, List<String> list2) {
        if (list1.size() == 0)
            return 0;
        Set<String> set = new HashSet<>();
        set.addAll(list1);
        set.addAll(list2);
        Log.d("Jaccard union of interests",String.valueOf(set));
        return new ArrayList<>(set).size();
    }

    private static double getIntersectOfLists(List<String> list1, List<String> list2) {
        if (list1.size() == 0)
            return 0;
        list1.retainAll(list2);

        return list1.size();
    }
}
