package com.unipi.p17050.mytourguide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.Others.Jaccard;
import com.unipi.p17050.mytourguide.Others.QuickSort;

import java.util.ArrayList;

import static com.unipi.p17050.mytourguide.Others.Jaccard.calculate;


public class GuideFragment extends Fragment {
   private static final String CLICKED="clicked";
   private FloatingActionButton startButton;
   private RecyclerView destinationsRV;
   private boolean clicked;
   private View view;
   private DatabaseReference mDatabase;
   private  ArrayList <Destination>  destinations=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view= inflater.inflate(R.layout.fragment_guide, container, false);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        destinationsRV=view.findViewById(R.id.destinationsRV);
        if (savedInstanceState != null) {
            clicked = savedInstanceState.getBoolean(CLICKED, false);
        } else {

            clicked=false;
        }
       initializeStartButton();
         return  view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CLICKED, clicked);
    }

    private void initializeStartButton(){
        startButton=view.findViewById(R.id.startButton);
        startButton.setSelected(clicked);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( startButton.isSelected()) {
                    clicked=false;
                    destinations.clear();
                    setUpAdapter(destinations);
                }
                else {
                    addDataToRV();
                    clicked=true;
                }

                startButton.setSelected(clicked);


            }
        });
    }
    private void addDataToRV(){
        final Profile profile =  ((MainActivity)getActivity()).getProfile();
        if(profile==null){
            Toast.makeText(getContext(),getString(R.string.Setup_profile),Toast.LENGTH_SHORT).show();
            return;
        }


        mDatabase.child("destinations").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("TAG","fetching destinations");
                 ArrayList<Double> scores=new ArrayList<>();


                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Destination destination=dataSnapshot.getValue(Destination.class);


                       if(profile.getAccessibility().contains(destination.getAccessibility())){

                          scores.add(Jaccard.calculate(profile,destination));

                          destinations.add(destination);

                   }

                }
                System.out.println(scores);
                QuickSort quickSort=new QuickSort(destinations,scores);
                destinations=  quickSort.startQuicksort();
                setUpAdapter(destinations);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


private void setUpAdapter(ArrayList<Destination> dest){
    DestinationsRecyclerViewAdapter adapter=new DestinationsRecyclerViewAdapter();
    adapter.setDestinations(dest);
    destinationsRV.setAdapter(adapter);
    destinationsRV.setLayoutManager(new LinearLayoutManager(getContext()));
}

}