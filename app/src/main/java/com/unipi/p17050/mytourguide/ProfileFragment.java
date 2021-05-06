package com.unipi.p17050.mytourguide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
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
import android.widget.Toast;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;

import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;

import com.unipi.p17050.mytourguide.Models.My_Location;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;

import net.cachapa.expandablelayout.ExpandableLayout;



public class ProfileFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    static final int REQUEST_CHECK_SETTINGS = 199;
    private View root;
    private Button culture_b, sport_b, religion_b, strolling_b;
    private SwitchMaterial switcher;
    private FusedLocationProviderClient mFusedclient;
    private LocationCallback mLocationCallback;
    private AutoCompleteTextView age_choices,transport_choices;
    private ExpandableLayout c_expandableLayout, e_expandableLayout, s_expandableLayout, r_expandableLayout;
    private Profile profile;
    private Slider slider;
    private MaterialCheckBox has_children,has_pushchair;
    private ProfilesViewModel viewModel;
    private Chip museum_chip, archaeological_chip, stadium_chip, sports_chip, churches_chip, monastery_chip, strolling_chip, shopping_chip, theater_chip, dining_chip;
    private  LocationManager manager ;

    @Override
    public void onResume() {
        super.onResume();
        ArrayAdapter<String> agedGroup = new ArrayAdapter<>(getContext(), R.layout.list_item, getResources().getStringArray(R.array.age_class_array));
        age_choices.setAdapter(agedGroup);
        ArrayAdapter<String> transportGroup = new ArrayAdapter<>(getContext(), R.layout.list_item, getResources().getStringArray(R.array.transport_class_array));
        transport_choices.setAdapter(transportGroup);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_profile, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfilesViewModel.class);
        mFusedclient = LocationServices.getFusedLocationProviderClient(getActivity());
        manager = (LocationManager) getActivity(). getSystemService(Context. LOCATION_SERVICE);
        if(isLocationPermissionGranded()){
            if(viewModel.isShown()==false && !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ){
                buildAlertMessageNoGps(getString(R.string.gps_suggest));
                viewModel.setShown(true);
            }
            else if(manager.isProviderEnabled( LocationManager.GPS_PROVIDER )){
                getLocation();
            }
        }
        initializeExpendables();
        initializeChips();
        switcher = root.findViewById(R.id.enable_distance);
        slider = root.findViewById(R.id.slider);
        age_choices = root.findViewById(R.id.age_choices);
        transport_choices=root.findViewById(R.id.transport_choices);
        has_children = root.findViewById(R.id.has_children);
        has_pushchair=root.findViewById(R.id.has_pushchair);



        viewModel.getProfile().observe(getViewLifecycleOwner(), new Observer<Profile>() {
            @Override
            public void onChanged(Profile prof) {
                Log.d(TAG, "profile changed");
                profile = prof;
                for (String interest : profile.getInterests()) {
                    switch (interest) {
                        case "museums":
                            museum_chip.setSelected(true);
                            museum_chip.setChecked(true);
                            break;
                        case "monument":
                            archaeological_chip.setSelected(true);
                            archaeological_chip.setChecked(true);
                            break;
                        case "stadiums":
                            stadium_chip.setSelected(true);
                            stadium_chip.setChecked(true);
                            break;
                        case "sports":
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
                        age_choices.setText(getResources().getStringArray(R.array.age_class_array)[1], false);
                        break;
                    case "Middle-aged":
                        age_choices.setText(getResources().getStringArray(R.array.age_class_array)[2], false);
                        break;
                    case "Elder":
                        age_choices.setText(getResources().getStringArray(R.array.age_class_array)[3], false);
                        break;
                    default:
                        age_choices.setText(getResources().getStringArray(R.array.age_class_array)[0], false);
                }
                switch (profile.getTransport()) {
                    case "Car":
                        transport_choices.setText(getResources().getStringArray(R.array.transport_class_array)[1], false);
                        break;
                    case "Means of transport":
                        transport_choices.setText(getResources().getStringArray(R.array.transport_class_array)[2], false);
                        break;
                    default:
                        transport_choices.setText(getResources().getStringArray(R.array.transport_class_array)[0], false);
                }
                has_children.setChecked(profile.isChildren());
                if(profile.isChildren()){
                    has_pushchair.setVisibility(View.VISIBLE);
                    has_pushchair.setChecked(profile.isPushchair());
                }
            }
        });

        viewModel.getDistance().observe(getViewLifecycleOwner(), new Observer<Float>() {
            @Override
            public void onChanged(Float distance) {
                if (distance < 1) {
                    switcher.setChecked(false);
                    slider.setEnabled(false);
                } else {
                    switcher.setChecked(true);
                    slider.setEnabled(true);
                    slider.setValue(distance);
                }
            }
        });
        viewModel.getLocation().observe(getViewLifecycleOwner(),my_location ->{
            if(my_location!=null){
                if(viewModel.getDistance().getValue()<1 && switcher.isChecked())
                    viewModel.setDistance(1);
            }
        });
        age_choices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1:
                        profile.setAge_group("Teen/Adult");
                        break;
                    case 2:
                        profile.setAge_group("Middle-aged");
                        break;
                    case 3:
                        profile.setAge_group("Elder");
                        break;
                    default:
                        profile.setAge_group("Undefined");
                }
                viewModel.setProfile(profile);
            }
        });

        transport_choices.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 1:
                    profile.setTransport("Car");
                    break;
                case 2:
                    profile.setTransport("Means of transport");
                    break;
                default:
                    profile.setTransport("Undefined");
            }
            viewModel.setProfile(profile);
        });

        switcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switcher.isChecked()) {
                    if (isLocationPermissionGranded()) {
                        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            buildAlertMessageNoGps(getString(R.string.gps_needed));
                        }
                        else
                            getLocation();
                    }
                    else {
                        Toast.makeText(getContext(), getString(R.string.gps_perrmissions), Toast.LENGTH_SHORT).show();
                        switcher.setChecked(false);
                    }
                } else {
                    slider.setEnabled(false);
                    slider.setValue(1);
                    viewModel.setDistance(-1);
                }

            }
        });

        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                viewModel.setDistance(slider.getValue());
            }

        });

        has_children.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setChildren(((CheckBox) v).isChecked());
                if(((CheckBox) v).isChecked())
                    has_pushchair.setVisibility(View.VISIBLE);
                else {
                    has_pushchair.setVisibility(View.GONE);
                    has_pushchair.setChecked(false);
                    profile.setPushchair(false);
                }
                viewModel.setProfile(profile);
            }
        });

        has_pushchair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setPushchair(((CheckBox) v).isChecked());
                viewModel.setProfile(profile);
            }
        });
        return root;
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
                button.setSelected(!button.isSelected());

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
                =  root.findViewById(R.id.culture_expandable);
        e_expandableLayout
                =  root.findViewById(R.id.entertainment_expendable);
        s_expandableLayout
                = root.findViewById(R.id.sport_expandable);
        r_expandableLayout
                =  root.findViewById(R.id.religion_expandable);

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
        dining_chip = root.findViewById(R.id.dining);
        chipClicked(museum_chip, "museums");
        chipClicked(archaeological_chip, "monument");
        chipClicked(sports_chip, "sports");
        chipClicked(stadium_chip, "stadiums");
        chipClicked(churches_chip, "churches");
        chipClicked(monastery_chip, "monasteries");
        chipClicked(strolling_chip, "strolling");
        chipClicked(shopping_chip, "shopping");
        chipClicked(theater_chip, "theater");
        chipClicked(dining_chip, "coffee/dining");
    }

    private boolean isLocationPermissionGranded() {
        Log.d(TAG, "getLocationPermission:getting permissions");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }



    @SuppressLint("MissingPermission")
    private void getLocation() {

        mFusedclient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            My_Location loc = new My_Location(location.getLatitude(),location.getLongitude());
                            viewModel.setLocation(loc);
                        } else {
                            final LocationRequest locationRequest = LocationRequest.create();
                            locationRequest.setInterval(10000);
                            locationRequest.setFastestInterval(5000);
                            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            mLocationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    if (locationResult == null) {
                                        return;
                                    }
                                    Location mLastKnownLocation = locationResult.getLastLocation();
                                    My_Location loc =new My_Location(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
                                    viewModel.setLocation(loc);
                                    mFusedclient.removeLocationUpdates(mLocationCallback);
                                }
                            };
                            mFusedclient.requestLocationUpdates(locationRequest, mLocationCallback, null);
                        }
                    }
                });
    }


    private void buildAlertMessageNoGps(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    getLocation();
                })
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    switcher.setChecked(false);
                    dialog.cancel();
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}

