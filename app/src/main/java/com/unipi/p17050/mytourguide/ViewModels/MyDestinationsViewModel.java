package com.unipi.p17050.mytourguide.ViewModels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.Profile;

import java.util.ArrayList;
import java.util.List;

public class MyDestinationsViewModel extends ViewModel {
    private String TAG = this.getClass().getSimpleName();
    private MutableLiveData<List<Destination>> destinations;
    private MutableLiveData<Boolean> clicked;

    public MutableLiveData<Boolean> getClicked() {
        if(clicked==null){
            clicked=new MutableLiveData<>();
            clicked.setValue(false);
        }
        return clicked;
    }

    public void setClicked(boolean clicked) {

            this.clicked.setValue(clicked);
    }




    public void setDestinations(List<Destination> destinations) {
        Log.i(TAG, "update list");
        this.destinations.setValue(destinations);
    }


    public MutableLiveData<List<Destination>> getDestinations()
    {
        Log.i(TAG, "Get destinations");
        if(destinations==null){
            destinations=new MutableLiveData<>();
            destinations.postValue(new ArrayList<Destination>());
        }
        return destinations;
    }
}
