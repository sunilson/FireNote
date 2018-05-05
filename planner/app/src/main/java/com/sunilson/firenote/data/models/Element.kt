package com.sunilson.firenote.data.models

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Element(val elementID: String,
                   private var _category: Category,
                   val noteType: String,
                   private var _color: Int,
                   private var _locked: Boolean,
                   val creationDate: Date = Date(),
                   private var _title: String = "New Element") : Parcelable, BaseObservable() {

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
            _locked= value
            notifyPropertyChanged(BR.locked)
        }

    var title: String
        @Bindable get() = _title
        set(value) {
            _title = value
            notifyPropertyChanged(BR.title)
        }
}
