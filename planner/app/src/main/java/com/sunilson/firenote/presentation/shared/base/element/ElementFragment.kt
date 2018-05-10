package com.sunilson.firenote.presentation.shared.base.element

interface ElementFragment {
    fun canLeave() : Boolean
    fun titleEditToggled(active : Boolean)
}