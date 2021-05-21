package com.unipi.p17050.mytourguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unipi.p17050.mytourguide.Models.Profile;
import com.unipi.p17050.mytourguide.ViewModels.ProfilesViewModel;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG_MY_FRAGMENT;
    private BottomNavigationView bnv;
    private final String TAG = this.getClass().getSimpleName();
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null)
            logout();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("destinations");
        mDatabase.keepSynced(true);
        getLocationPermission();
        bnv = findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(navListener);
        ProfilesViewModel viewModel = new ViewModelProvider(this).get(ProfilesViewModel.class);
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            setFragment(item.getItemId());
            return true;
        }
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

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission:getting permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);

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
}