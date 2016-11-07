package com.pro3.planner;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by linus_000 on 05.11.2016.
 */

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Activate Disk Persistence of Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
