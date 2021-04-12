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
    private FirebaseUser user;

   private boolean clicked;
   private View view;
   private DatabaseReference mDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view= inflater.inflate(R.layout.fragment_guide, container, false);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        destinationsRV=view.findViewById(R.id.destinationsRV);
        if (savedInstanceState != null) {
            clicked = savedInstanceState.getBoolean(CLICKED, false);
            System.out.println(clicked);
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
        final Profile[] profile = {new Profile()};
        user= FirebaseAuth.getInstance().getCurrentUser();
        mDatabase.child("Profiles").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profile[0] = snapshot.getValue(Profile.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final ArrayList<Float> scores=new ArrayList<>();
         final ArrayList <Destination>  destinations=new ArrayList<>();

        mDatabase.child("destinations").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Destination destination=dataSnapshot.getValue(Destination.class);

                   for(int accessiblity: profile[0].getAccessibility()){
                       if(accessiblity==destination.getAccessibility()){
                           Log.d("asdasd","asdasd");
                          scores.add(Jaccard.calculate(profile[0],destination));
                          destinations.add(destination);
                          break;
                       }
                   }

                }
                QuickSort quickSort=new QuickSort(destinations,scores);
                DestinationsRecyclerViewAdapter adapter=new DestinationsRecyclerViewAdapter();
                adapter.setDestinations(quickSort.startQuicksort());
                destinationsRV.setAdapter(adapter);
                destinationsRV.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        System.out.println(destinations);


    }

}