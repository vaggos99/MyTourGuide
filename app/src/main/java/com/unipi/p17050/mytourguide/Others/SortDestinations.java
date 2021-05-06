package com.unipi.p17050.mytourguide.Others;


import android.location.Location;
import android.widget.ArrayAdapter;

import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.My_Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortDestinations  {


    public static void sortBydDistance(ArrayList<Destination> dest,My_Location location,int index){
        if(dest.size()<=1||index==dest.size()-1)
            return;
       float min=500000000;
       int p=index+1;
       for(int i=index+1 ;i<dest.size();i++){
           float[] resultArray = new float[99];
           Location.distanceBetween(location.getLatitude(), location.getLongitude(), dest.get(i).getLocation().getLatitude(), dest.get(i).getLocation().getLongitude(), resultArray);
            if(resultArray[0]/1000.0<min){
                p=i;
                min=resultArray[0]/1000;
            }
       }

       Collections.swap(dest, index+1, p);
       sortBydDistance(dest, dest.get(index+1).getLocation(),index+1);

    }

}
