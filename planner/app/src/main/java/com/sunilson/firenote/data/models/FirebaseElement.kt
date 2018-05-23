package com.sunilson.firenote.data.models

import java.util.*

data class FirebaseElement (
        var elementID: String,
        var categoryName: String,
        var categoryID: String,
        var noteType: String,
        var color: Int,
        var locked: Boolean,
        var creationDate: Date,
        var title: String
)