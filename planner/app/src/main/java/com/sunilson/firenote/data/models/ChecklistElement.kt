package com.sunilson.firenote.data.models

import com.google.firebase.database.Exclude
import com.sunilson.firenote.presentation.shared.base.adapters.AdapterElement

data class ChecklistElement(@Exclude var id : String? = "", var text: String = "", var finished: Boolean = false) : AdapterElement{
    override val compareByString: String
        get() = id!!
}