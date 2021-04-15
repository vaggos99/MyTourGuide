package com.unipi.p17050.mytourguide;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.unipi.p17050.mytourguide.ViewModels.MyDestinationsViewModel;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.unipi.p17050.mytourguide.Others.Jaccard.calculate;


public class GuideFragment extends Fragment {
   private static final String CLICKED="clicked";
   private FloatingActionButton startButton;
   private RecyclerView destinationsRV;

   private View view;
   private DatabaseReference mDatabase;
   private MyDestinationsViewModel destviewModel;
    private  Profile profile;
    private  ArrayList <Destination>  destinations;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view= inflater.inflate(R.layout.fragment_guide, container, false);
        mDatabase= FirebaseDatabase.getInstance().getReference();
        destinationsRV=view.findViewById(R.id.destinationsRV);
        startButton=view.findViewById(R.id.startButton);
        ProfilesViewModel viewModel =  new ViewModelProvider(requireActivity()).get(ProfilesViewModel.class);
        destviewModel= new ViewModelProvider(requireActivity()).get(MyDestinationsViewModel.class);
        viewModel.getProfile().observe(getViewLifecycleOwner(), new Observer<Profile>() {
            @Override
            public void onChanged(Profile prof) {
                profile=prof;
            }
        });
        destviewModel.getDestinations().observe(getViewLifecycleOwner(), new Observer<List<Destination>>() {
            @Override
            public void onChanged(List<Destination> dest) {
                destinations= (ArrayList<Destination>) dest;
                setUpAdapter(destinations);
            }
        });

        destviewModel.getClicked().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                startButton.setSelected(aBoolean);

            }
        });

       initializeStartButton();
         return  view;
    }



    private void initializeStartButton(){



        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG","Button clicked");
                if( startButton.isSelected()) {
                    destviewModel.setClicked(false);
                    destinations.clear();
                    destviewModel.setDestinations(destinations);

                }
                else {
                    addDataToRV();
                    destviewModel.setClicked(true);
                }



            }
        });
    }
    private void addDataToRV(){

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
                           Log.d("TAG","destination found");
                          scores.add(Jaccard.calculate(profile,destination));

                          destinations.add(destination);

                   }

                }

                QuickSort quickSort=new QuickSort(destinations,scores);

                destviewModel.setDestinations( quickSort.startQuicksort());

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