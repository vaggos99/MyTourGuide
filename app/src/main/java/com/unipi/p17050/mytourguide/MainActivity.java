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

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bnv;
    private static final String TAG = "MainActivity";
    private  static  final  int ERROR_DIALOG_REQUEST=9001;
    private static final String FRAGMENT_NUM="fragment_num";
    private static final ProfileFragment pf=new ProfileFragment();
    private static final GuideFragment gf=new GuideFragment();
    private static final MapFragment mf=new MapFragment();
    private Fragment selectedFragment;
    private int fr_num;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        bnv=findViewById(R.id.bottom_navigation);
        bnv.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState != null) {
            fr_num = savedInstanceState.getInt(FRAGMENT_NUM, R.id.profile);
            setFragment(fr_num);
        } else {

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, mf, "3").hide(mf).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, gf, "2").hide(gf).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,pf, "1").commit();
            selectedFragment=pf;
            fr_num=R.id.profile;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        user= FirebaseAuth.getInstance().getCurrentUser();
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FRAGMENT_NUM, fr_num);

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
            setFragment(item.getItemId());
            return true;
        }
    };

    private void setFragment(int id){

        switch(id){
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().hide(selectedFragment).show(pf).commit();
                selectedFragment=pf;
                break;
            case R.id.start:
                getSupportFragmentManager().beginTransaction().hide(selectedFragment).show(gf).commit();
                selectedFragment=gf;
                break;
            case R.id.map:
                if (isServicesOk()) {
                    getSupportFragmentManager().beginTransaction().hide(selectedFragment).show(mf).commit();
                    selectedFragment=mf;
                }
                break;

        }
        fr_num=id;
    }
    private void logout(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }
}