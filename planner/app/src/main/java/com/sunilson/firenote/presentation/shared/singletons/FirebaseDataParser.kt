package com.sunilson.firenote.presentation.shared.singletons

import com.google.firebase.database.DataSnapshot
import com.sunilson.firenote.data.models.Category
import com.sunilson.firenote.data.models.Element
import java.util.*

object FirebaseDataParser {
    fun parseElement(snapshot: DataSnapshot) : Element {
        return Element(
                snapshot.key,
                Category(snapshot.child("categoryName").value as String, snapshot.child("categoryID").value as String),
                snapshot.child("noteType").value as String,
                (snapshot.child("color").value as Long).toInt(),
                snapshot.child("locked").value as Boolean,
                snapshot.child("creationDate").getValue(Date::class.java)!!,
                snapshot.child("title").value as String
        )
    }
}