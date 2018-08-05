package com.sunilson.firenote.presentation.elements

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface BaseElementPresenterContract {
    interface Presenter {
        fun loadElementData(elementID: String, parent: String?)
        fun lockElement(locked: Boolean)
        fun updateElement(element: Element)
    }

    interface View : IBaseView {
        val element: Element?
        fun toggleTitleEdit(active : Boolean)
        fun elementChanged(element: Element)
        fun elementRemoved()
    }
}