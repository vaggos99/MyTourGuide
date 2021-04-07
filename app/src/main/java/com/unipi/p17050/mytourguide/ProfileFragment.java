package com.unipi.p17050.mytourguide;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.p17050.mytourguide.Models.Profile;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {
    private  View root;
    private Button culture_b,sport_b,religion_b,strolling_b;
    private CheckBox hard_c,medium_c,easy_c;
    private ImageButton info_b;
    private  Profile profile= new Profile();
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root= inflater.inflate(R.layout.fragment_profile, container, false);
        user= FirebaseAuth.getInstance().getCurrentUser();
        info_b=root.findViewById(R.id.info_Button);
        initializeButtons();
        initializeCheckboxes();
        info_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
showMessage(getString(R.string.infos),getString(R.string.accessibility_infos));
            }
        });




        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Profiles").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue(Profile.class)!=null) {
                        profile = snapshot.getValue(Profile.class);
                        for (String interest:profile.getInterests()){
                            switch (interest){
                                case "culture":
                                    culture_b.setSelected(true);
                                    break;
                                case "religion":
                                    religion_b.setSelected(true);
                                    break;
                                case "strolling":
                                    strolling_b.setSelected(true);
                                    break;
                                case "sport":
                                    sport_b.setSelected(true);
                                    break;
                            }
                        }
                        for (int accessibility:profile.getAccessibility()){
                            switch (accessibility){
                                case 1:
                                    easy_c.setChecked(true);
                                    break;
                                case 2:
                                    medium_c.setChecked(true);
                                    break;
                                case 3:
                                    hard_c.setChecked(true);;
                                    break;

                            }
                        }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("DataRead","Error when reading data");
            }
        });

        return root;
    }

    private void onCheckboxClicked(CheckBox checkBox,int value) {
        // Is the view now checked?
      checkBox.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              boolean checked = ((CheckBox) v).isChecked();
              if (checked)
                  profile.addAccessibility(value);
              else
                  profile.removeAccessibility(value);
              mDatabase.child("Profiles").child(user.getUid()).child("accessibility").setValue(profile.getAccessibility());
          }
      });

        }


    private void buttonColorChange(Button button,String value){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( button.isSelected()) {
                    button.setSelected(false);
                    profile.removeInterest(value);
                }
                else {
                    button.setSelected(true);
                    profile.addInterest(value);
                }
                mDatabase.child("Profiles").child(user.getUid()).child("interests").setValue(profile.getInterests());
            }
        });
    }
    private void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
private void initializeButtons(){
    culture_b=root.findViewById(R.id.culture_button);
    religion_b=root.findViewById(R.id.religion_button);
    strolling_b=root.findViewById(R.id.strolling_button);
    sport_b=root.findViewById(R.id.sport_button);
    buttonColorChange(culture_b,"culture");
    buttonColorChange(religion_b,"religion");
    buttonColorChange(strolling_b,"strolling");
    buttonColorChange(sport_b,"sport");
}
    private void initializeCheckboxes(){
        easy_c=root.findViewById(R.id.checkBox_Easy);
        medium_c=root.findViewById(R.id.checkBox_Medium);
        hard_c=root.findViewById(R.id.checkBox_Hard);
        onCheckboxClicked(easy_c,1);
        onCheckboxClicked(medium_c,2);
        onCheckboxClicked(hard_c,3);
    }
}