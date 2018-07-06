package com.sunilson.firenote.presentation.bin

import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract
import com.sunilson.firenote.presentation.shared.base.IBaseView
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList

interface BinPresenterContract {
    interface View : IBaseView, HasElementList {
    }

    interface Presenter : ElementContentPresenterContract.Presenter {
        fun restoreElement(id: String, parent: String? = null)
        fun clearElements(parent: String? = null)
    }
}