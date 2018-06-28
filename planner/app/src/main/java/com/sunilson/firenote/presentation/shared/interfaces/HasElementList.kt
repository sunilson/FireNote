package com.sunilson.firenote.presentation.shared.interfaces

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.elementList.ElementRecyclerAdapter

interface HasElementList {
    val adapter: ElementRecyclerAdapter
    fun elementAdded(element: Element) = adapter.add(element)
    fun elementChanged(element: Element) = adapter.update(element)
    fun elementRemoved(element: Element) = adapter.remove(element)
}