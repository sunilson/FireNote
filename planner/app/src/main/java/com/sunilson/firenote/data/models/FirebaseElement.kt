package com.sunilson.firenote.data.models

import java.util.*

data class FirebaseElement (
        var elementID: String = "",
        var categoryName: String = "",
        var categoryID: String = "",
        var noteType: String = "",
        var color: Int = 0,
        var locked: Boolean = false,
        var timeStamp: Long = 0,
        var title: String = ""
)