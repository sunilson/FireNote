package com.sunilson.firenote.presentation.bin

import android.sax.Element
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface BinPresenterContract {
    interface View : IBaseView {
        fun elementAdded(element: Element)
        fun elementChanged(element: Element)
        fun elementRemoved(element: Element)
    }

    interface Presenter {
        fun loadData()
    }
}