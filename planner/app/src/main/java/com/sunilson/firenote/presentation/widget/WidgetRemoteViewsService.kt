package com.sunilson.firenote.presentation.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.sunilson.firenote.data.IRepository
import dagger.android.AndroidInjection
import javax.inject.Inject

class WidgetRemoteViewsService : RemoteViewsService() {

    @Inject
    lateinit var repository : IRepository

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return WidgetRemoteFactory(applicationContext, repository, p0!!)
    }
}