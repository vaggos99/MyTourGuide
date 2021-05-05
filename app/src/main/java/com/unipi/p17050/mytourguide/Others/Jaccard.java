package com.unipi.p17050.mytourguide.Others;

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
            ArrayList destination_age_group = new ArrayList(destination.getAge_group());
            ArrayList transport_list=new ArrayList(destination.getTransport());
            double union =getUnionOfLists(profile_interests, destination_interests) ;
            double intersect =  (1+(double)profile_interests.size()/(double)destination_interests.size())*getIntersectOfLists(profile_interests, destination_interests);

            if (destination_age_group.contains(profile.getAge_group()) && !profile.getAge_group().equals("Undefined")) {
                intersect++;
                union += destination_age_group.size();

            }
            else if(!profile.getAge_group().equals("Undefined"))
                union += 1.5*destination_age_group.size();

            if (transport_list.contains(profile.getTransport()) && !profile.getTransport().equals("Undefined")) {
                intersect++;
                union += transport_list.size();
            }
            else if(!profile.getTransport().equals("Undefined"))
                union += 1.5*transport_list.size();

            if ((profile.getAge_group().equals("Elder") || profile.isPushchair())) {
                if (destination.isEasy_access())
                    intersect++;
                union++;
            }

            if(profile.isChildren()){
                if(destination.isChildren())
                    intersect++;

                union++;
            }
            System.out.println(intersect+" "+union);
            return intersect / union;



    }


    private static double getUnionOfLists(List<String> list1, List<String> list2) {

        Set<String> set = new HashSet<>();
        set.addAll(list1);
        set.addAll(list2);
        System.out.println(set);
        return new ArrayList<>(set).size();
    }

    private static double getIntersectOfLists(List<String> list1, List<String> list2) {


        list1.retainAll(list2);

        return list1.size();
    }
}
