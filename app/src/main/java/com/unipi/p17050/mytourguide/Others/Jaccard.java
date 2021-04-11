package com.unipi.p17050.mytourguide.Others;

import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.Profile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Jaccard {
    public float calculate(Profile profile, Destination destination)
    {

        ArrayList<String> union= (ArrayList<String>) getUnionOfLists(profile.getInterests(),destination.getCategory());
        ArrayList<String> intersect= (ArrayList<String>) getIntersectOfLists(profile.getInterests(),destination.getCategory());
        return union.size()/intersect.size();
    }
    private List<String> getUnionOfLists(List<String> list1, List<String> list2) {

        Set<String> set = new HashSet<>();
        set.addAll(list1);
        set.addAll(list2);

        return new ArrayList<>(set);
    }
    private static List<String> getIntersectOfLists(List<String> list1, List<String> list2) {
        list1.retainAll(list2);

        return list1;
    }
}
