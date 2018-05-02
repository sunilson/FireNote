package com.sunilson.firenote.data.models

import java.util.*
import kotlin.collections.HashMap

class Checklist( var elements: HashMap<String, ChecklistElement> = hashMapOf(),
                 elementID: String,
                 category: Category,
                 noteType: String,
                 color: Int,
                 locked: Boolean,
                 creationDate: Date = Date(),
                 title: String = "New Element") : Element(elementID, category, noteType, color, locked, creationDate, title)