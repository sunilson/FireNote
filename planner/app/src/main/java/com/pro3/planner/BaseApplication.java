package com.pro3.planner;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by linus_000 on 05.11.2016.
 */

public class BaseApplication extends Application {

    public Context mainContext = null;
    private Activity myCurrentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();

        //Activate Disk Persistence of Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    public boolean getInternetConnected() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public Activity getCurrentActivity(){
        return myCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.myCurrentActivity = mCurrentActivity;
    }
}
