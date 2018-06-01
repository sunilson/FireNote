package com.sunilson.firenote.presentation.elementDialog

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface ElementDialogPresenterContract {
    interface Presenter {
        fun addElement(element: Element)
    }

    interface View : IBaseView{
    }
}