package com.sunilson.firenote.presentation.shared.presenters

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable

interface BaseContract {
    interface IBasePresenter : LifecycleObserver {
        fun setView(view: IBaseView)
    }

    interface IBaseView {
        fun addObserver(presenter: IBasePresenter)
    }
}

abstract class BasePresenter : BaseContract.IBasePresenter {

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