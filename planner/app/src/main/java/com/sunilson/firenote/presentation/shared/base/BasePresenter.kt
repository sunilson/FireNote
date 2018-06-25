package com.sunilson.firenote.presentation.shared.base

import android.appwidget.AppWidgetManager
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import com.sunilson.firenote.presentation.shared.DisposableDelegate
import com.sunilson.firenote.presentation.shared.showToast
import com.sunilson.firenote.presentation.widget.FirenoteWidgetProvider
import io.reactivex.disposables.CompositeDisposable

interface IBaseView : LifecycleOwner {
    val mContext: Context?
    fun addObserver(presenter: BasePresenter) = lifecycle.addObserver(presenter)
    fun showError(message: String?) {
        toggleLoading(false)
        mContext?.showToast(message)
    }
    fun showSuccess(message: String?) {
        toggleLoading(false)
        mContext?.showToast(message)
    }
    fun toggleLoading(loading: Boolean, message: String? = null)
}

abstract class BasePresenter(view: IBaseView) : LifecycleObserver {

    private val view : IBaseView = view
    protected val disposable: CompositeDisposable by DisposableDelegate()

    init {
        view.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    open fun onStop() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onStart() {}

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    open fun onDestroy() {
        disposable.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    open fun onCreate() {}

    protected fun updateWidget() {
        val intent = Intent(view.mContext, FirenoteWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(view.mContext).getAppWidgetIds(ComponentName(view.mContext, FirenoteWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        view.mContext!!.sendBroadcast(intent)
    }
}