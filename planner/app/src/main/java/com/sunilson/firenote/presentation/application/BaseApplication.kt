package com.sunilson.firenote.presentation.application

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.support.v4.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.sunilson.firenote.presentation.application.di.DaggerApplicationComponent
import dagger.android.*
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject

class BaseApplication : Application(), HasActivityInjector, HasSupportFragmentInjector, HasBroadcastReceiverInjector, HasServiceInjector {

    @Inject
    lateinit var dispatchingFragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var dispatchingBroadcastInjector: DispatchingAndroidInjector<BroadcastReceiver>

    @Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

    override fun onCreate() {
        super.onCreate()

        DaggerApplicationComponent.builder().application(this).build().inject(this)
        //Activate Disk Persistence of Firebase
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingActivityInjector
    override fun supportFragmentInjector(): AndroidInjector<Fragment> = dispatchingFragmentInjector
    override fun broadcastReceiverInjector(): AndroidInjector<BroadcastReceiver> = dispatchingBroadcastInjector
    override fun serviceInjector(): AndroidInjector<Service> = dispatchingServiceInjector
}