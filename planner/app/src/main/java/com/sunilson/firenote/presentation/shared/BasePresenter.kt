package com.sunilson.firenote.presentation.shared

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent

interface BaseContract {
    interface IBasePresenter<in V> : LifecycleObserver {
        fun setView(view: V)
        fun clearSubscriptions()
    }

    interface IBaseView {
        fun addObserver(presenter: IBasePresenter<IBaseView>)
    }
}


abstract class BasePresenter<in V> : BaseContract.IBasePresenter<V> {
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    abstract fun onStop()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    abstract fun onStart()

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    abstract fun onDestroy()

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    abstract fun onCreate()
}