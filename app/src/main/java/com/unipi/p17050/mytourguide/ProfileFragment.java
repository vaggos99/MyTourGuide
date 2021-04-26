package com.unipi.p17050.mytourguide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;


import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;

import net.cachapa.expandablelayout.ExpandableLayout;


public class ProfileFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    private View root;
    private Button culture_b, sport_b, religion_b, strolling_b;
    private SwitchMaterial switcher;

    private AutoCompleteTextView age_choices;
    private ExpandableLayout c_expandableLayout, e_expandableLayout, s_expandableLayout, r_expandableLayout;
    private Profile profile;
    private Slider slider;
    private MaterialCheckBox has_children;
    private ProfilesViewModel viewModel;
    private Chip museum_chip, archaeological_chip, stadium_chip, sports_chip, churches_chip, monastery_chip, strolling_chip, shopping_chip, theater_chip,dining_chip;

    @Override
    public void onResume() {
        super.onResume();
        ArrayAdapter<String> agedGroup = new ArrayAdapter<>(getContext(), R.layout.list_item, getResources().getStringArray(R.array.age_class_array));
        age_choices.setAdapter(agedGroup);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        
        initializeExpendables();
        initializeChips();
        switcher = root.findViewById(R.id.enable_distance);
        slider = root.findViewById(R.id.slider);

        age_choices = root.findViewById(R.id.age_choices);
        has_children = root.findViewById(R.id.has_children);


        viewModel = new ViewModelProvider(requireActivity()).get(ProfilesViewModel.class);

        viewModel.getProfile().observe(getViewLifecycleOwner(), new Observer<Profile>() {
            @Override
            public void onChanged(Profile prof) {
                profile = prof;
                for (String interest : profile.getInterests()) {
                    switch (interest) {
                        case "museums":
                            museum_chip.setSelected(true);
                            museum_chip.setChecked(true);
                            break;
                        case "archaeological places":
                            archaeological_chip.setSelected(true);
                            archaeological_chip.setChecked(true);
                            break;
                        case "stadiums":
                            stadium_chip.setSelected(true);
                            stadium_chip.setChecked(true);
                            break;
                        case "sport places":
                            sports_chip.setSelected(true);
                            sports_chip.setChecked(true);
                            break;
                        case "churches":
                            churches_chip.setSelected(true);
                            churches_chip.setChecked(true);
                            break;
                        case "monasteries":
                            monastery_chip.setSelected(true);
                            monastery_chip.setChecked(true);
                            break;
                        case "strolling":
                            strolling_chip.setSelected(true);
                            strolling_chip.setChecked(true);
                            break;
                        case "shopping":
                            shopping_chip.setSelected(true);
                            shopping_chip.setChecked(true);
                            break;
                        case "theater":
                            theater_chip.setSelected(true);
                            theater_chip.setChecked(true);
                            break;
                        case "coffee/dining":
                            dining_chip.setSelected(true);
                            dining_chip.setChecked(true);
                            break;
                    }
                }
                switch (profile.getAge_group()) {
                    case "Teen/Adult":
                        age_choices.setText(getString(R.string.Teen_Adult), false);
                        break;
                    case "Middle-aged":
                        age_choices.setText(getString(R.string.Middle_aged), false);
                        break;
                    case "Elder":
                        age_choices.setText(getString(R.string.Elder), false);
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

    private void chipClicked(Chip chip, String value) {
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chip.isSelected()) {
                    chip.setSelected(false);
                    chip.setChecked(false);
                    profile.removeInterest(value);
                } else {
                    chip.setSelected(true);
                    chip.setChecked(true);
                    profile.addInterest(value);
                }


                viewModel.setProfile(profile);
            }

        });
    }

    private void buttonClicked(Button button, ExpandableLayout expandableLayout) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (button.isSelected()) {
                    button.setSelected(false);
                } else {
                    button.setSelected(true);
                }

                expandableLayout.toggle();
            }
        });
    }

    private void initializeExpendables() {
        culture_b = root.findViewById(R.id.culture_button);
        religion_b = root.findViewById(R.id.religion_button);
        strolling_b = root.findViewById(R.id.entertainment_button);
        sport_b = root.findViewById(R.id.sport_button);

        c_expandableLayout
                = (ExpandableLayout) root.findViewById(R.id.culture_expandable);
        e_expandableLayout
                = (ExpandableLayout) root.findViewById(R.id.entertainment_expendable);
        s_expandableLayout
                = (ExpandableLayout) root.findViewById(R.id.sport_expandable);
        r_expandableLayout
                = (ExpandableLayout) root.findViewById(R.id.religion_expandable);

        buttonClicked(culture_b, c_expandableLayout);
        buttonClicked(religion_b, r_expandableLayout);
        buttonClicked(strolling_b, e_expandableLayout);
        buttonClicked(sport_b, s_expandableLayout);
    }

    private void initializeChips() {
        museum_chip = root.findViewById(R.id.museum);
        archaeological_chip = root.findViewById(R.id.archaeological);
        stadium_chip = root.findViewById(R.id.stadiums);
        sports_chip = root.findViewById(R.id.sports_places);
        churches_chip = root.findViewById(R.id.churches);
        monastery_chip = root.findViewById(R.id.monastery);
        strolling_chip = root.findViewById(R.id.strolling);
        shopping_chip = root.findViewById(R.id.shopping);
        theater_chip = root.findViewById(R.id.theater);
        dining_chip=root.findViewById(R.id.dining);
        chipClicked(museum_chip, "museums");
        chipClicked(archaeological_chip, "archaeological places");
        chipClicked(sports_chip, "sport places");
        chipClicked(stadium_chip, "stadiums");
        chipClicked(churches_chip, "churches");
        chipClicked(monastery_chip, "monasteries");
        chipClicked(strolling_chip, "strolling");
        chipClicked(shopping_chip, "shopping");
        chipClicked(theater_chip, "theater");
        chipClicked(dining_chip,"coffee/dining");
    }
}