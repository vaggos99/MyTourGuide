package com.unipi.p17050.mytourguide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.facebook.FacebookSdk.getApplicationContext;


public class HomeFragment extends Fragment {


    private  final String TAG = this.getClass().getSimpleName();
    private  View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.athensInfoButton).setOnClickListener(v -> {
            startActivity(AthensInfoActivity.class);
        });
        view.findViewById(R.id.dest_Button).setOnClickListener(v -> {
            startActivity(DestinationsActivity.class);
        });
        view.findViewById(R.id.myFavorites).setOnClickListener(v -> {
            startActivity(FavoritesActivity.class);
        });

        return view;
    }

    private void startActivity(Class activity){
        Intent intent = new Intent(getActivity(), activity);
        startActivity(intent);
    }
}