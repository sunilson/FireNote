package com.sunilson.firenote.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.Element
import io.reactivex.disposables.CompositeDisposable

class WidgetRemoteFactory(val context: Context, val repository: IRepository, val intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    val disposable: CompositeDisposable = CompositeDisposable()
    private var data = mutableListOf<Element>()

    override fun onCreate() {
        disposable.add(repository.loadAllElements(FirebaseAuth.getInstance().currentUser!!.uid).subscribe { list ->
            data.clear()
            list.forEach { data.add(it) }
            AppWidgetManager
                    .getInstance(context)
                    .notifyAppWidgetViewDataChanged(
                            intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                    AppWidgetManager.INVALID_APPWIDGET_ID),
                            R.id.widget_listview)
        })
    }

    override fun onDestroy() {
        disposable.dispose()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun onDataSetChanged() {
        val elements = repository.loadAllElements(FirebaseAuth.getInstance().currentUser!!.uid).blockingGet()
        data.clear()
        elements.forEach { data.add(it) }
    }

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(p0: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_list_item)
        views.setTextViewText(R.id.elementList_title, data[p0].title)
        views.setTextViewText(R.id.elementList_category, data[p0].category.name)
        return views
    }

    override fun getCount(): Int = data.size
    override fun getViewTypeCount(): Int = 1
}