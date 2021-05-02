package com.unipi.p17050.mytourguide;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class LauncherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
