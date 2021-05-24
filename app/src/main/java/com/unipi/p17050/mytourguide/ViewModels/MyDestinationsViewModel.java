package com.unipi.p17050.mytourguide.ViewModels;

import android.location.Location;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.My_Location;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.Others.Jaccard;
import com.unipi.p17050.mytourguide.Others.SortDestinations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyDestinationsViewModel extends ViewModel {
    private final String TAG = this.getClass().getSimpleName();
    private MutableLiveData<List<Destination>> destinations;


    public void setDestinations(Profile profile, float distance, My_Location my_location) {
        Log.i(TAG, "update list");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("destinations");
        Log.d(TAG, "Get destinations");
        if (destinations == null) {
            Log.i(TAG, " destinations NULL");
            destinations = new MutableLiveData<>();
        }
        if (profile == null) {
            Log.i(TAG, " profile NULL");
            ArrayList<Destination> dest = new ArrayList<>();
            destinations.postValue(dest);
            return;
        }

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG", "fetching destinations");
                ArrayList<Double> scores = new ArrayList<>();
                ArrayList<Destination> dest = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Destination destination = dataSnapshot.getValue(Destination.class);
                    Log.d(TAG,destination.getName());
                    if (distance >= 1 && my_location != null) {
                        float[] resultArray = new float[99];
                        Location.distanceBetween(my_location.getLatitude(), my_location.getLongitude(), destination.getLocation().getLatitude(), destination.getLocation().getLongitude(), resultArray);
                        if (resultArray[0] / 1000 > distance)
                            continue;
                    }
                    double score = Jaccard.calculate(profile, destination);
                    if (score >= 1.0 - (float) profile.getInterests().size() / 8.0||score>=0.8) {
                        scores.add(score);
                        dest.add(destination);

                    }


                }

                if (dest.size() > 0) {

                    if (my_location == null) {
                        int max = scores.indexOf(Collections.max(scores));
                        Collections.swap(dest, 0, max);
                        SortDestinations.sortBydDistance(dest, dest.get(0).getLocation(), 0);
                    } else {
                        SortDestinations.sortBydDistance(dest, my_location, -1);
                    }
                    if ((profile.isChildren() || profile.getAge_group().equals("Elder"))) {

                        ArrayList<Destination> dest_temp = new ArrayList<>();

                        dest_temp.add(dest.get(0));

                        for (int i = 1; i < scores.size(); i++) {
                            float[] resultArray = new float[99];
                            Location.distanceBetween(dest.get(i).getLocation().getLatitude(), dest.get(i).getLocation().getLongitude(), dest.get(0).getLocation().getLatitude(), dest.get(0).getLocation().getLongitude(), resultArray);

                            scores.set(i, scores.get(i) * 3000 / resultArray[0]);
                            if (scores.get(i) >= (1.0 - (float) profile.getInterests().size() / 5.0)||scores.get(i)>=0.8) {
                                dest_temp.add(dest.get(i));
                            }
                        }

                        if (dest_temp.size() >= 5) {
                            dest_temp.subList(5, dest_temp.size()).clear();
                            destinations.postValue(dest_temp);
                        } else
                            destinations.postValue(dest_temp);
                    } else {
                        if (dest.size() >= 7) {
                            dest.subList(7, dest.size()).clear();
                            destinations.postValue(dest);
                        } else
                            destinations.postValue(dest);
                    }
                } else
                    destinations.postValue(dest);
                Log.d(TAG,String.valueOf(scores));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public MutableLiveData<List<Destination>> getDestinations() {
        if (destinations == null) {
            destinations = new MutableLiveData<>();
            ArrayList<Destination> dest = new ArrayList<>();
            destinations.postValue(dest);
        }
        return destinations;
    }
}