package com.unipi.p17050.mytourguide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unipi.p17050.mytourguide.Models.My_Location;

import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;



public class MainActivity extends AppCompatActivity {

    private String TAG_MY_FRAGMENT;
    private final String TAG = this.getClass().getSimpleName();
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Fragment selectedFragment = null;
    private FusedLocationProviderClient mFusedclient;
    private LocationCallback mLocationCallback;
    private  ProfilesViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            logout();
        mFusedclient = LocationServices.getFusedLocationProviderClient(this);
        viewModel = new ViewModelProvider(this).get(ProfilesViewModel.class);
        LocationManager manager = (LocationManager) this. getSystemService(Context. LOCATION_SERVICE);
        if(getLocationPermission()){
            if(!viewModel.isShown() && !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) &&viewModel.getLocation().getValue()==null){
                buildAlertMessageNoGps(getString(R.string.gps_suggest));
                viewModel.setShown(true);
            }
            else if(manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) && viewModel.getLocation().getValue()==null){
                getLocation();
            }
        }
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("destinations");
        mDatabase.keepSynced(true);
        getLocationPermission();
        BottomNavigationView bnv = findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(navListener);

        if (user != null)
            viewModel.getProfile();
        if (savedInstanceState == null) {
            selectedFragment = new HomeFragment();
            TAG_MY_FRAGMENT = "home";
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, TAG_MY_FRAGMENT).commit();
        } else {
            TAG_MY_FRAGMENT = savedInstanceState.getString("TAG_MY_FRAGMENT", "home");
            selectedFragment = getSupportFragmentManager().findFragmentByTag(TAG_MY_FRAGMENT);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "checking location permissions");

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    buildAlertMessageNoGps(getString(R.string.gps_suggest));
                    Log.d(TAG, "permission given");
                }
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if ( user.isAnonymous()) {
                    Log.d(TAG,"Anonymous user");
                    DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Profiles").child(user.getUid());
                    database.removeValue();
                    user.delete();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    logout();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;

    }

    public boolean isServicesOk() {
        Log.d(TAG, "isServicesOk: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOk: Google play services are working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOk: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.error_map), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        setFragment(item.getItemId());
        return true;
    };

    private void setFragment(int id) {

        switch (id) {
            case R.id.home:
                TAG_MY_FRAGMENT = "home";
                selectedFragment = new HomeFragment();
                break;
            case R.id.profile:
                TAG_MY_FRAGMENT = "profile";
                selectedFragment = new ProfileFragment();
                break;
            case R.id.start:
                TAG_MY_FRAGMENT = "guide";
                selectedFragment = new GuideFragment();
                break;
            case R.id.map:
                if (isServicesOk()) {
                    TAG_MY_FRAGMENT = "map";
                    selectedFragment = new MapFragment();
                }
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment, TAG_MY_FRAGMENT).commit();

    }

    private boolean getLocationPermission() {
        Log.d(TAG, "getLocationPermission:getting permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;

        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    private void logout() {


        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("TAG_MY_FRAGMENT", TAG_MY_FRAGMENT);

    }


    @SuppressLint("MissingPermission")
    public void getLocation() {

        mFusedclient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            My_Location loc = new My_Location(location.getLatitude(),location.getLongitude());
                            viewModel.setLocation(loc);
                        } else {
                            final LocationRequest locationRequest = LocationRequest.create();
                            locationRequest.setExpirationDuration(30000);
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
                                    Log.d(TAG,"Location found remove updates");
                                    mFusedclient.removeLocationUpdates(mLocationCallback);
                                }
                            };
                            Log.d(TAG,"Location requesting updates ");
                            mFusedclient.requestLocationUpdates(locationRequest, mLocationCallback, null);
                        }
                    }
                });
    }

    private void buildAlertMessageNoGps(String message) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    getLocation();
                })
                .setNegativeButton(R.string.no, (dialog, id) -> {
                    dialog.cancel();
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}