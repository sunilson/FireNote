package com.sunilson.firenote.presentation.application

import android.app.Activity
import android.app.Application
import android.support.v4.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.sunilson.firenote.presentation.application.di.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class BaseApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        DaggerApplicationComponent.builder().application(this).build().inject(this)
        //Activate Disk Persistence of Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector
}