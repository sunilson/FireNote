package com.sunilson.firenote.presentation.shared.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.sunilson.firenote.presentation.shared.DisposableDelegate
import com.sunilson.firenote.presentation.shared.showToast
import io.reactivex.disposables.CompositeDisposable

interface IBaseView : LifecycleOwner {
    val mContext: Context
    fun addObserver(presenter: BasePresenter) = lifecycle.addObserver(presenter)
    fun showError(message: String?) {
        toggleLoading(false)
        mContext.showToast(message)
    }
    fun showSuccess(message: String?) {
        toggleLoading(false)
        mContext.showToast(message)
    }
    fun toggleLoading(loading: Boolean, message: String? = null)
}

abstract class BasePresenter(view: IBaseView) : LifecycleObserver {

    protected val disposable: CompositeDisposable by DisposableDelegate()

    init { view.addObserver(this) }

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
}