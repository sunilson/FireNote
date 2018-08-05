package com.sunilson.firenote.presentation.bin

import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract
import com.sunilson.firenote.presentation.shared.base.IBaseView
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList

interface BinPresenterContract {
    interface View : IBaseView, HasElementList {
        var parent: String?
    }

    interface Presenter : ElementContentPresenterContract.Presenter {
        fun restoreElement(id: String)
        fun clearElements()
        fun deleteElement(id: String)
    }
}