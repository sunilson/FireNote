package com.sunilson.firenote.presentation.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.core.graphics.ColorUtils
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.typeBundle
import com.sunilson.firenote.presentation.shared.typeChecklist
import com.sunilson.firenote.presentation.shared.typeNote
import io.reactivex.disposables.CompositeDisposable

class WidgetRemoteFactory(val context: Context, val repository: IRepository, val intent: Intent) : RemoteViewsService.RemoteViewsFactory {

    val disposable: CompositeDisposable = CompositeDisposable()
    private var data = mutableListOf<Element>()

    override fun onCreate() {
        if (FirebaseAuth.getInstance().currentUser != null) {
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
    }

    override fun onDestroy() {
        disposable.dispose()
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(p0: Int): Long = p0.toLong()

    override fun onDataSetChanged() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            val elements = repository.loadAllElements(FirebaseAuth.getInstance().currentUser!!.uid).blockingGet()
            data.clear()
            elements.forEach { data.add(it) }
        }
    }

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(p0: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_list_item)
        views.setTextViewText(R.id.elementList_title, data[p0].title)
        views.setTextViewText(R.id.elementList_category, data[p0].category.name)
        views.setInt(R.id.widget_elementList_icon_holder, "setBackgroundColor", data[p0].color)
        views.setInt(R.id.widget_elementList_content, "setBackgroundColor", ColorUtils.setAlphaComponent(data[p0].color, 80))
        when(data[p0].noteType) {
            typeNote ->  views.setImageViewResource(R.id.widget_elementList_icon, R.drawable.ic_note_white_24dp)
            typeChecklist ->  views.setImageViewResource(R.id.widget_elementList_icon, R.drawable.ic_done_all_white_24dp)
            typeBundle ->  views.setImageViewResource(R.id.widget_elementList_icon, R.drawable.ic_list_white_24dp)

        }
        if(data[p0].locked)views.setViewVisibility(R.id.widget_elementList_lock, View.VISIBLE)

        val intent = Intent()
        intent.putExtra("openElement", data[p0].elementID)
        intent.action = Intent.ACTION_VIEW
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        views.setOnClickFillInIntent(R.id.widget_list_item, intent)
        return views
    }

    override fun getCount(): Int = data.size
    override fun getViewTypeCount(): Int = 1
}