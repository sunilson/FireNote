package com.sunilson.firenote.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Element(var elementID: String,
                   val category: Category,
                   val noteType: String,
                   val color: Int,
                   val locked: Boolean,
                   val creationDate: Date = Date(),
                   val title: String = "New Element") : Parcelable
