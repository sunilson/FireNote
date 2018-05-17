package com.sunilson.firenote.presentation.shared.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Context
import android.widget.Toast
import com.sunilson.firenote.presentation.shared.other.DisposableDelegate
import io.reactivex.disposables.CompositeDisposable

interface IBaseView : LifecycleOwner {
    val mContext: Context
    fun addObserver(presenter: BasePresenter) = lifecycle.addObserver(presenter)
    fun showError(message: String?) = Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
    fun showSuccess(message: String?) = Toast.makeText(mContext, message, Toast.LENGTH_LONG).show()
    fun toggleLoading(loading: Boolean, message: String? = null)
    fun showTutorial()
}



abstract class BasePresenter : LifecycleObserver {

    protected val disposable: CompositeDisposable by DisposableDelegate()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    abstract fun onStop()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    abstract fun onStart()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun SuperOnDestroy() {
        disposable.dispose()
        onDestroy()
    }

    abstract fun onDestroy()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    abstract fun onCreate()
}