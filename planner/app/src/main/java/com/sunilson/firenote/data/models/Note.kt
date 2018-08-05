package com.sunilson.firenote.data.models

import java.util.*

class Note( var text: String,
            elementID: String,
            category: Category,
            noteType: String,
            color: Int,
            locked: Boolean,
            timestamp: Long = Date().time,
            title: String = "New Element") : Element(elementID, category, noteType, color, locked, timestamp, title) {

}