package com.unipi.p17050.mytourguide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.PublicKey;

public class MainActivity extends AppCompatActivity {
    MapFragment mapFragment;
    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bnv;
    private static final String TAG = "MainActivity";
    private  static  final  int ERROR_DIALOG_REQUEST=9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        bnv=findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user ==null){
            logout();
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                String login_type=sharedPreferences.getString("login_type",null);
                mAuth.getInstance().signOut();
                if(login_type.equals("facebook"))
                    LoginManager.getInstance().logOut();
                logout();

        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;

    }
public  boolean isServicesOk(){
    Log.d(TAG,"isServicesOk: checking google services version");
    int available= GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
    if(available== ConnectionResult.SUCCESS){
        Log.d(TAG,"isServicesOk: Google play services are working");
        return  true;
    }
    else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
        Log.d(TAG,"isServicesOk: an error occured but we can fix it");
        Dialog dialog=GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this,available,ERROR_DIALOG_REQUEST);
        dialog.show();
    }
    else{
        Toast.makeText(getApplicationContext(),getString(R.string.error_map),Toast.LENGTH_LONG).show();
    }
    return false;
}
    private BottomNavigationView.OnNavigationItemSelectedListener navListener= new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment=null;
            switch(item.getItemId()){
                case R.id.profile:
                    selectedFragment=new ProfileFragment();
                    break;
                case R.id.start:
                    selectedFragment=new GuideFragment();
                    break;
                case R.id.map:
                    if (isServicesOk()) {
                        mapFragment = new MapFragment();
                        selectedFragment=mapFragment;
                    }
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
            return true;
        }
    };
    private void logout(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}