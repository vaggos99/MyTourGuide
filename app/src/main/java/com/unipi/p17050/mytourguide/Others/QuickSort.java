package com.unipi.p17050.mytourguide.Others;

import com.unipi.p17050.mytourguide.Models.Destination;

import java.util.ArrayList;

public class QuickSort {
    private ArrayList<Destination> dest;
    private ArrayList<Float> scores;

    public QuickSort(ArrayList<Destination> dest, ArrayList<Float> scores) {
        this.dest = dest;
        this.scores = scores;
    }
    public ArrayList<Destination> startQuicksort(){
        quicksort(0,scores.size()-1);
        for(float k :scores){
            System.out.println(k);
        }
        return dest;
    }
    private  int partition(int p, int q) {
        float x =scores.get(p);
        int i=p;
        for(int j=p+1;j<=q;j++) {
            if(scores.get(j)<=x) {
                i++;
                float temp=scores.get(i);
                scores.set(i,scores.get(j));
                scores.set(j,temp);
                Destination temp1=dest.get(i);
                dest.set(i,dest.get(j));
                dest.set(j,temp1);
            }

        }
        float temp=scores.get(p);
        scores.set(p,scores.get(i));
        scores.set(i,temp);
        Destination temp1=dest.get(p);
        dest.set(p,dest.get(i));
        dest.set(i,temp1);
        return i;

    }


    private void quicksort(int p,int r) {
        if(p<r) {
            int q=partition(p,r);
            quicksort(p,q-1);
            quicksort(q+1,r);
        }

    }
}
