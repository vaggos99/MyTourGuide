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
import com.unipi.p17050.mytourguide.Others.QuickSort;
import java.util.ArrayList;
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
                  System.out.println(dataSnapshot.getKey());
                    Destination destination = dataSnapshot.getValue(Destination.class);
                    float[] resultArray = new float[99];
                    double score=Jaccard.calculate(profile, destination);
                    try {
                        Location.distanceBetween(my_location.getLatitude(), my_location.getLongitude(), destination.getLocation().getLatitude(), destination.getLocation().getLongitude(), resultArray);
                        if (resultArray[0] / 1000 < distance || distance < 1) {
                            Log.d("TAG", "destination calculate");

                            if (profile.isChildren() || profile.getAge_group().equals("Elder")) {
                                score = score * 2000 / resultArray[0];
                            }
                            if (score >= 0.50) {
                                scores.add(score);
                                dest.add(destination);
                            }
                        }
                    }
                    catch (NullPointerException e){
                        Log.d("TAG", "destination calculate without location");
                        if(score>=0.50) {
                            scores.add(score);
                            dest.add(destination);
                        }

                    }

                }

                QuickSort quickSort = new QuickSort(dest, scores);

                dest=quickSort.startQuicksort();
                System.out.println(scores);

                destinations.postValue(dest);

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