package com.sunilson.firenote.presentation.shared.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.widget.Toast
import com.sunilson.firenote.presentation.shared.other.DisposableDelegate
import io.reactivex.disposables.CompositeDisposable

interface IBaseView {
    fun addObserver(presenter: BasePresenter)
    fun showError(message: String?)
    fun showSuccess(message: String?)
    fun showTutorial()
    fun toggleLoading(loading: Boolean, message: String? = null)
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