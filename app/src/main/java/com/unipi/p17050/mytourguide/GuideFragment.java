package com.unipi.p17050.mytourguide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class GuideFragment extends Fragment {
   private static final String CLICKED="clicked";
   private FloatingActionButton startButton;
   private boolean clicked;
   private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view= inflater.inflate(R.layout.fragment_guide, container, false);

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
                    clicked=true;
                }
                startButton.setSelected(clicked);

            }
        });
    }

}