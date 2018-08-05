package com.sunilson.firenote.data.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(val name: String, val id: String) : Parcelable