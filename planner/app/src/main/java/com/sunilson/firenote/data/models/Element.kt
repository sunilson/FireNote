package com.sunilson.firenote.data.models

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.os.Parcelable
import com.sunilson.firenote.BR
import com.sunilson.firenote.presentation.shared.NotifyPropertyChangedDelegate
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
open class Element constructor(
        var elementID: String = "",
        private var _category: Category = Category("", ""),
        val noteType: String = "note",
        private var _color: Int = 123,
        private var _locked: Boolean = false,
        val timeStamp: Long = 0,
        private var _title: String = "New Element",
        val parent: String? = null) : Parcelable, BaseObservable() {

    @IgnoredOnParcel
    @get:Bindable
    var category: Category by NotifyPropertyChangedDelegate(_category, BR.category)

    @IgnoredOnParcel
    @get:Bindable
    var color: Int by NotifyPropertyChangedDelegate(_color, BR.color)

    @IgnoredOnParcel
    @get:Bindable
    var locked: Boolean by NotifyPropertyChangedDelegate(_locked, BR.locked)

    @IgnoredOnParcel
    @get:Bindable
    var title: String by NotifyPropertyChangedDelegate(_title, BR.title)

    @IgnoredOnParcel
    val creationDate: Date
        get() = Date(timeStamp)
}