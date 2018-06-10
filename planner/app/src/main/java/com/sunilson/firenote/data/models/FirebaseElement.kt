package com.sunilson.firenote.data.models

data class FirebaseElement(
        var elementID: String = "",
        var categoryName: String = "",
        var categoryID: String = "",
        var noteType: String = "",
        var color: Int = 0,
        var locked: Boolean = false,
        var timeStamp: Long = 0,
        var title: String = ""
) {
    fun parseElement(): Element {
        return Element(
                this.elementID,
                Category(this.categoryName, this.categoryID),
                this.noteType,
                this.color,
                this.locked,
                this.timeStamp,
                this.title
        )
    }
}