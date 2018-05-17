package com.sunilson.firenote.presentation.shared.base.element.interfaces

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface BaseElementPresenterContract {
    interface Presenter {
        fun loadElementData()
        fun lockElement(locked: Boolean)
    }

    interface View : IBaseView {
        val element: Element
        fun toggleTitleEdit(active : Boolean)
        fun elementChanged(element: Element)
        fun elementRemoved()
    }
}