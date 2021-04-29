package com.unipi.p17050.mytourguide;

import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;
import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.My_Location;
import com.unipi.p17050.mytourguide.Models.Profile;

import com.unipi.p17050.mytourguide.ViewModels.MyDestinationsViewModel;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;

import java.util.ArrayList;
import java.util.List;


public class GuideFragment extends Fragment {

    private RecyclerView destinationsRV;

    private View view;

    private MyDestinationsViewModel destviewModel;

    private ArrayList<Destination> destinations;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_guide, container, false);

        destinationsRV = view.findViewById(R.id.destinationsRV);

        ProfilesViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProfilesViewModel.class);
        destviewModel = new ViewModelProvider(requireActivity()).get(MyDestinationsViewModel.class);
        float distance =viewModel.getDistance().getValue();
        My_Location my_location=viewModel.getLocation().getValue();

        Profile profile =viewModel.getProfile().getValue();
        destviewModel.setDestinations(profile,distance,my_location);


        destviewModel.getDestinations().observe(getViewLifecycleOwner(), new Observer<List<Destination>>() {
            @Override
            public void onChanged(List<Destination> dest) {
                destinations = (ArrayList<Destination>) dest;
                if(destinations==null)
                    Toast.makeText(getContext(),"Yoy have to fill your profile",Toast.LENGTH_SHORT).show();
                else
                    setUpAdapter(destinations);
            }
        });



        return view;
    }


    private void setUpAdapter(ArrayList<Destination> dest) {
        DestinationsRecyclerViewAdapter adapter = new DestinationsRecyclerViewAdapter(getActivity());
        adapter.setDestinations(dest);
        destinationsRV.setAdapter(adapter);
        destinationsRV.setLayoutManager(new LinearLayoutManager(getContext()));
    }

}