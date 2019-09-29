package com.sunilson.firenote.data.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import android.os.Parcelable
import com.sunilson.firenote.BR
import com.sunilson.firenote.presentation.shared.NotifyPropertyChangedDelegate
import com.sunilson.firenote.presentation.shared.base.adapters.AdapterElement
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
open class Element constructor(
        var elementID: String = "",
        private var _category: Category = Category("", ""),
        val noteType: String = "note",
        private var _color: Int = -769226,
        private var _locked: Boolean = false,
        val timeStamp: Long = Date().time,
        private var _title: String = "New Element",
        var parent: String? = null,
        var deleted: Boolean = false
) : Parcelable, BaseObservable(), AdapterElement {

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

    fun showLock(): Boolean = locked && !deleted
    fun showRestoreIcon(): Boolean = deleted

    fun copy(): Element = Element(elementID, _category, noteType, _color, _locked, timeStamp, _title, parent)

    override val compareByString: String
        get() = elementID
}