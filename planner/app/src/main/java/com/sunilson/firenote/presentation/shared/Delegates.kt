package com.sunilson.firenote.presentation.shared

import androidx.databinding.BaseObservable
import io.reactivex.disposables.CompositeDisposable
import kotlin.properties.ReadWriteProperty
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

class NotifyPropertyChangedDelegate<T>(private var value: T, private val propertyId: Int) : ReadWriteProperty<BaseObservable, T> {
    override fun getValue(thisRef: BaseObservable, property: KProperty<*>): T = value

    override fun setValue(thisRef: BaseObservable, property: KProperty<*>, value: T) {
        if (this.value !== value) {
            this.value = value
            thisRef.notifyPropertyChanged(propertyId)
        }
    }
}
