package com.sunilson.firenote.presentation.shared.base.element

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface ElementContentPresenterContract {
    interface Presenter {
        fun loadElementData()
    }

    interface View : IBaseView {
        fun getElement(): Element
    }
}