package com.unipi.p17050.mytourguide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;
import com.unipi.p17050.mytourguide.Models.My_Location;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.concurrent.Executor;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();
    static final int REQUEST_CHECK_SETTINGS = 199;
    private View root;
    private Button culture_b, sport_b, religion_b, strolling_b;
    private SwitchMaterial switcher;
    private FusedLocationProviderClient mFusedclient;
    private LocationCallback mLocationCallback;
    private AutoCompleteTextView age_choices;
    private ExpandableLayout c_expandableLayout, e_expandableLayout, s_expandableLayout, r_expandableLayout;
    private Profile profile;
    private Slider slider;
    private MaterialCheckBox has_children;
    private ProfilesViewModel viewModel;
    private Chip museum_chip, archaeological_chip, stadium_chip, sports_chip, churches_chip, monastery_chip, strolling_chip, shopping_chip, theater_chip, dining_chip;

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
        mFusedclient = LocationServices.getFusedLocationProviderClient(getActivity());
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
                Log.d(TAG, "profile changed");
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


                has_children.setChecked(profile.isChildren());
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
                if (switcher.isChecked()) {
                    if (isLocationPermissionGranded()) {

                        askForGps();
                    }
                    else
                        Toast.makeText(getContext(),getString(R.string.gps_perrmissions),Toast.LENGTH_SHORT).show();
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
        chipClicked(archaeological_chip, "archaeological places");
        chipClicked(sports_chip, "sport places");
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
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private void askForGps() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {


            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    Log.d(TAG, "askForGps:GPS already enabled");
                    slider.setEnabled(true);
                    if(viewModel.getDistance().getValue()<1)
                        viewModel.setDistance(1);
                    getLocation();
                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        getActivity(),
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            Toast.makeText(getContext(), "Sorry there is a problem!You cannot use this...", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d(TAG, "askForGps:GPS  enabled");
                        // All required changes were successfully made
                        getLocation();
                        slider.setEnabled(true);
                        if(viewModel.getDistance().getValue()<1)
                            viewModel.setDistance(1);
                        Toast.makeText(getActivity(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                        break;

                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(TAG, "askForGps:GPS  denied");
                        switcher.setChecked(false);
                        Toast.makeText(getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        switcher.setChecked(false);
                        break;

                }
                break;
        }
    }
}

