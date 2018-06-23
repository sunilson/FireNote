package com.sunilson.firenote.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.sunilson.firenote.R
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.shared.singletons.LocalSettingsManager
import dagger.android.AndroidInjection
import javax.inject.Inject

class FirenoteWidgetProvider @Inject constructor() : AppWidgetProvider() {

    @Inject
    lateinit var localSettingsManager: LocalSettingsManager

    override fun onReceive(context: Context?, intent: Intent?) {
        AndroidInjection.inject(this, context)
        super.onReceive(context, intent)
    }

    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        appWidgetIds?.forEach {
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            val views = RemoteViews(context?.packageName, R.layout.widget_layout)
            views.setOnClickPendingIntent(R.id.widget_button, pendingIntent)
            views.setRemoteAdapter(R.id.widget_listview, Intent(context, WidgetRemoteViewsService::class.java))

            appWidgetManager?.updateAppWidget(it, views)
        }
    }
}