package com.sunilson.firenote.presentation.shared.base.element

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface BaseElementPresenterContract {
    interface Presenter {
        fun loadElementData()
        fun lockElement(locked: Boolean)
    }

    interface View : IBaseView {
        fun getElement() : Element
        fun toggleTitleEdit(active : Boolean)
        fun elementChanged(element: Element)
        fun elementRemoved()
    }
}