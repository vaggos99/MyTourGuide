package com.unipi.p17050.mytourguide.Others;

import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.Profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Jaccard {
    public static double calculate(Profile profile, Destination destination)
    {

        ArrayList profile_interests=new ArrayList(profile.getInterests());
        ArrayList destination_interests=new ArrayList(destination.getCategory());
        String profile_age_group=profile.getAge_group();
        ArrayList destination_age_group=new ArrayList(destination.getAge_group());

        double union= getUnionOfLists(profile_interests,destination_interests)+destination_age_group.size();
        double intersect=  getIntersectOfLists(profile_interests,destination_interests);
        if(destination_age_group.contains(profile_age_group))
            intersect++;

        return intersect/union;
    }



    private static double getUnionOfLists(List<String> list1, List<String> list2) {

        Set<String> set = new HashSet<>();
        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set).size();
    }
    private static double getIntersectOfLists(List<String> list1, List<String> list2) {


        list1.retainAll(list2);

        return list1.size();
    }
}
