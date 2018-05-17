package com.sunilson.firenote.data.models

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
open class Element(var elementID: String,
                   private var _category: Category,
                   val noteType: String = "note",
                   private var _color: Int = 123,
                   private var _locked: Boolean = false,
                   val creationDate: Date = Date(),
                   private var _title: String = "New Element",
                   val parent: String? = null) : Parcelable, BaseObservable() {

    var category: Category
        @Bindable get() = _category
        set(value) {
            _category = value
            notifyPropertyChanged(BR.category)
        }

    var color: Int
        @Bindable get() = _color
        set(value) {
            _color = value
            notifyPropertyChanged(BR.color)
        }

    var locked: Boolean
        @Bindable get() = _locked
        set(value) {
            _locked = value
            notifyPropertyChanged(BR.locked)
        }

    var title: String
        @Bindable get() = _title
        set(value) {
            _title = value
            notifyPropertyChanged(BR.title)
        }
}
