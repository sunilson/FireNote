package com.sunilson.firenote.presentation.widget

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return WidgetRemoteFactory(applicationContext, p0!!)
    }
}