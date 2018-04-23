package com.sunilson.firenote.presentation.shared

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

interface BaseContract {
    interface IBasePresenter : LifecycleObserver {
        fun setView(view: IBaseView)
        fun clearSubscriptions()
    }

    interface IBaseView {
        fun addObserver(presenter: IBasePresenter)
    }
}


abstract class BasePresenter : BaseContract.IBasePresenter {
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    abstract fun onStop()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    abstract fun onStart()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    abstract fun onDestroy()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    abstract fun onCreate()
}