package com.sunilson.firenote.data.models

import java.util.*

data class Element(val noteID: String,
                   val category: Category,
                   val noteType: String,
                   val color: Int,
                   val locked: Boolean,
                   val creationDate: Date = Date(),
                   val title: String = "New Element")
