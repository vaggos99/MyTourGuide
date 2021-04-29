package com.unipi.p17050.mytourguide.ViewModels;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.p17050.mytourguide.Models.My_Location;
import com.unipi.p17050.mytourguide.Models.Profile;

public class ProfilesViewModel extends ViewModel {
    private String TAG = this.getClass().getSimpleName();
    private MutableLiveData<Profile> profile;
   private MutableLiveData<My_Location> location=new MutableLiveData<>();
    private MutableLiveData<Float> distance=new MutableLiveData<Float>();

    public MutableLiveData<My_Location> getLocation() {
        if(location.getValue()==null)
            location.setValue(new My_Location());
        return location;
    }

    public void setLocation(My_Location location) {
        this.location.postValue(location);
    }

    public MutableLiveData<Float> getDistance() {
        if(distance.getValue()==null)
            distance.postValue((float) -1);
        return distance;
    }

    public void setDistance(float distance) {
        this.distance.postValue(distance);
    }

    public MutableLiveData<Profile> getProfile() {
        Log.i(TAG, "Get profile");
        if (profile == null) {
            profile = new MutableLiveData<>();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Profiles").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                        profile.postValue(snapshot.getValue(Profile.class));
                    else profile.postValue(new Profile());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return profile;
    }


    public void setProfile(Profile profile) {
        Log.i(TAG, "update profile");
        this.profile.setValue(profile);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Profiles").child(user.getUid()).setValue(profile);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.i(TAG, "ViewModel Destroyed");
    }


}
