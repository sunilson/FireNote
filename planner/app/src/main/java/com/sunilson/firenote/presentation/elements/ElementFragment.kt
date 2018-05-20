package com.sunilson.firenote.presentation.elements

interface ElementFragment {
    fun canLeave() : Boolean
    fun titleEditToggled(active : Boolean)
}