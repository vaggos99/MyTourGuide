package com.unipi.p17050.mytourguide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.unipi.p17050.mytourguide.Models.Destination;
import com.unipi.p17050.mytourguide.Models.My_Location;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.ViewModels.MyDestinationsViewModel;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static com.google.android.libraries.places.api.model.TypeFilter.*;

public class MapFragment extends Fragment implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private View view;


    private  final String TAG = this.getClass().getSimpleName();
    private static final float DEFAULT_ZOOM = 15f;


    private SupportMapFragment supportMapFragment;
    private GoogleMap mMap;

    private  MyDestinationsViewModel destViewModel;
    private FusedLocationProviderClient mFusedclient;
    private NavigationView navigationView;
    private Location mLastKnownLocation;
    private LocationCallback mLocationCallback;
    private MaterialSearchBar materialSearchBar;
    private View mapView;
    private DrawerLayout navDrawer;
    private ArrayList<Marker> blue_markers=new ArrayList<Marker>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_map, container, false);
        destViewModel = new ViewModelProvider(requireActivity()).get(MyDestinationsViewModel.class);
        ProfilesViewModel viewModel = new ViewModelProvider(requireActivity()).get(ProfilesViewModel.class);
        My_Location my_location=viewModel.getLocation().getValue();
        float distance =viewModel.getDistance().getValue();
        Profile profile =viewModel.getProfile().getValue();
        destViewModel.setDestinations(profile,distance,my_location);

        initMap();
        mapView = supportMapFragment.getView();
        mFusedclient = LocationServices.getFusedLocationProviderClient(getActivity());
        materialSearchBar = view.findViewById(R.id.searchBar);
        navDrawer = view.findViewById(R.id.drawer);
        navigationView = view.findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);


        search();

        return view;

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is ready");
        mMap = googleMap;
        if (isLocationPermissionGranded()) {
            mMap.setMyLocationEnabled(true);
        } else {
            snackbar(getString(R.string.gps_perrmissions));
        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 40, 180);
        }
        if(isLocationPermissionGranded()) {
            //check if gps is enabled and request user to enable it
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

            SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
            Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

            task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                    getDeviceLocation();
                }
            });
            task.addOnFailureListener(getActivity(), new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (e instanceof ResolvableApiException) {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        try {
                            resolvable.startResolutionForResult(getActivity(), 51);
                        } catch (IntentSender.SendIntentException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            });
        }
        getLocations();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 51) {
            if (resultCode == RESULT_OK) {
                getDeviceLocation();
            }
        }
    }


    private boolean isLocationPermissionGranded() {
        Log.d(TAG, "getLocationPermission:getting permissions");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }


    private void initMap() {
        Log.d(TAG, "initializing map");

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(this);

    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        mFusedclient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
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
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                        mFusedclient.removeLocationUpdates(mLocationCallback);
                                    }
                                };
                                mFusedclient.requestLocationUpdates(locationRequest, mLocationCallback, null);

                            }
                        } else {
                            Toast.makeText(getActivity(), "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getLocations(){
        destViewModel.getDestinations().observe(getViewLifecycleOwner(), new Observer<List<Destination>>() {
            @Override
            public void onChanged(List<Destination> dest) {
                for(Marker m :blue_markers){
                    m.remove();
                }
                blue_markers.clear();
                if(dest!=null&&dest.size()>0){
                    for(Destination destination:dest){
                        
                        String title;
                        if(Locale.getDefault().getDisplayLanguage().contains("English"))
                            title=destination.getName();
                        else
                            title=destination.getName_gr();
                        LatLng latLng = new LatLng(destination.getLocation().getLatitude(), destination.getLocation().getLongitude());
                        blue_markers.add(mMap.addMarker(new MarkerOptions().position(latLng).title(title) .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))));
                    }
                }

            }
        });
    }
    private void snackbar(String message) {
        Snackbar.make(view.findViewById(R.id.drawer), message, Snackbar.LENGTH_SHORT).show();
    }

    private void search() {
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                String location = text.toString();
                List<Address> addressList = null;
                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (IndexOutOfBoundsException e) {
                        Toast.makeText(getContext(), "Zero results", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @SuppressLint("WrongConstant")
            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {

                    // If the navigation drawer is not open then open it, if its already open then close it.
                    if (!navDrawer.isDrawerOpen(Gravity.START)) navDrawer.openDrawer(Gravity.START);
                    else navDrawer.closeDrawer(Gravity.END);

                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.disableSearch();

                }
            }
        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            default:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
        }
        //close navigation drawer
        navDrawer.closeDrawer(GravityCompat.START);
        return true;
    }
}


