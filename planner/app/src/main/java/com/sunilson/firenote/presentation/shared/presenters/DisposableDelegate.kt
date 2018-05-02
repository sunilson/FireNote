package com.sunilson.firenote.presentation.shared.presenters

import io.reactivex.disposables.CompositeDisposable
import kotlin.reflect.KProperty

class DisposableDelegate {
    private var disposable : CompositeDisposable = CompositeDisposable()
    operator fun getValue(thisRef: Any?, prop: KProperty<*>): CompositeDisposable {
        if(disposable.isDisposed) disposable = CompositeDisposable()
        return disposable
    }

    operator fun setValue(thisRef: Any?, prop: KProperty<*>, value: CompositeDisposable) {
        disposable = value
    }
}