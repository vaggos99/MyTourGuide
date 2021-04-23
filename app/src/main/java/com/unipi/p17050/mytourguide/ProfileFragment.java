package com.unipi.p17050.mytourguide;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;

import java.util.ArrayList;
import java.util.List;


public class ProfileFragment extends Fragment {
    private View root;
    private Button culture_b, sport_b, religion_b, strolling_b;
    private SwitchMaterial switcher;
    private TextInputLayout age_class;
    private AutoCompleteTextView age_choices;
    private Profile profile;
    private Slider slider;
    private MaterialCheckBox has_children;
    private ProfilesViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);


        initializeButtons();

        switcher = root.findViewById(R.id.enable_distance);
        slider = root.findViewById(R.id.slider);
        age_class = root.findViewById(R.id.age_class);
        age_choices = root.findViewById(R.id.age_choices);
        has_children = root.findViewById(R.id.has_children);
        ExpandableRelativeLayout expandableLayout
                = (ExpandableRelativeLayout) root.findViewById(R.id.expandableLayout);



// set base position which is close position
        expandableLayout.setClosePosition(500);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfilesViewModel.class);
        ArrayAdapter<String> agedGroup = new ArrayAdapter<>(getContext(), R.layout.list_item, getResources().getStringArray(R.array.age_class_array));
        age_choices.setAdapter(agedGroup);
        viewModel.getProfile().observe(getViewLifecycleOwner(), new Observer<Profile>() {
            @Override
            public void onChanged(Profile prof) {
                profile = prof;
                for (String interest : profile.getInterests()) {
                    switch (interest) {
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
                switch (profile.getAge_group()) {
                    case "Teen/Adult":
                        age_choices.setText(getString(R.string.Teen_Adult),false);
                        break;
                    case "Middle-aged":
                        age_choices.setText(getString(R.string.Middle_aged),false);
                        break;
                    case "Elder":
                        age_choices.setText(getString(R.string.Elder),false);
                        break;
                }
                if (profile.getDistance() < 0) {
                    switcher.setChecked(false);
                    slider.setEnabled(false);
                } else {
                    switcher.setChecked(true);
                    slider.setEnabled(true);
                    slider.setValue(profile.getDistance());
                }

                has_children.setChecked(profile.isChildren());
            }
        });
        age_choices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        profile.setAge_group("Teen/Adult");
                        break;
                    case 1:
                        profile.setAge_group("Middle-aged");
                        break;
                    case 2:
                        profile.setAge_group("Elder");
                        break;
                }
                viewModel.setProfile(profile);
            }
        });


        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switcher.isChecked())
                    slider.setEnabled(true);

                else {
                    slider.setEnabled(false);
                    slider.setValue(1);
                    profile.setDistance(-1);
                    viewModel.setProfile(profile);
                }

            }
        });

        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                profile.setDistance(slider.getValue());
                viewModel.setProfile(profile);
            }

        });

        onCheckboxClicked(has_children);
        return root;
    }

    private void onCheckboxClicked(CheckBox checkBox) {
        // Is the view now checked?
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setChildren(((CheckBox) v).isChecked());
                viewModel.setProfile(profile);
            }
        });

    }


    private void buttonColorChange(Button button, String value) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.isSelected()) {
                    button.setSelected(false);
                    profile.removeInterest(value);
                } else {
                    button.setSelected(true);
                    profile.addInterest(value);
                }


                viewModel.setProfile(profile);
            }
        });
    }

    private void initializeButtons() {
        culture_b = root.findViewById(R.id.culture_button);
        religion_b = root.findViewById(R.id.religion_button);
        strolling_b = root.findViewById(R.id.strolling_button);
        sport_b = root.findViewById(R.id.sport_button);
        buttonColorChange(culture_b, "culture");
        buttonColorChange(religion_b, "religion");
        buttonColorChange(strolling_b, "strolling");
        buttonColorChange(sport_b, "sport");
    }

}