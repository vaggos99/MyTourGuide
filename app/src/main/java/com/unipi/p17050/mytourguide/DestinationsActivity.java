package com.unipi.p17050.mytourguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.p17050.mytourguide.Models.Destination;

import java.util.ArrayList;

public class DestinationsActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    private  DestinationsRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinations);
        TabLayout tabLayout=findViewById(R.id.tabLayout);
        RecyclerView destinationsRV = findViewById(R.id.recyclerView);
        adapter = new DestinationsRecyclerViewAdapter(this);
        setRecyclerView("museums");
        destinationsRV.setAdapter(adapter);
        destinationsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG,String.valueOf(tab.getPosition()));
                switch (tab.getPosition()){
                    case 0:
                        setRecyclerView("museums");
                        break;
                    case 1:
                        setRecyclerView("monument");
                        break;
                    case 2:
                        setRecyclerView("stadiums");
                        break;
                    case 3:
                        setRecyclerView("sports");
                        break;
                    case 4:
                        setRecyclerView("churches");
                        break;
                    case 5:
                        setRecyclerView("monasteries");
                        break;
                    case 6:
                        setRecyclerView("strolling");
                        break;
                    case 7:
                        setRecyclerView("shopping");
                        break;
                    case 8:
                        setRecyclerView("theater");
                        break;
                    case 9:
                        setRecyclerView("coffee/dining");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setRecyclerView(final String category){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("destinations");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Destination> dest = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Destination destination = dataSnapshot.getValue(Destination.class);
                    if(destination.getCategory().contains(category))
                        dest.add(destination);
                }
                adapter.setDestinations(dest);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}