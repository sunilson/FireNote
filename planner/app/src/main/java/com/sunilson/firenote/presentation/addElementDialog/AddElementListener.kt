package com.sunilson.firenote.presentation.addElementDialog

import com.sunilson.firenote.data.models.Element

interface AddElementListener {
    fun addElement(element: Element)
    fun elementAdded(element: Element)
}