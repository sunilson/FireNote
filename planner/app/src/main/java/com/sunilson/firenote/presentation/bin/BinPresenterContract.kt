package com.sunilson.firenote.presentation.bin

import android.sax.Element
import com.sunilson.firenote.presentation.shared.base.BaseContract

interface BinPresenterContract {
    interface View : BaseContract.IBaseView {
        fun elementAdded(element: Element)
        fun elementChanged(element: Element)
        fun elementRemoved(element: Element)
    }

    interface Presenter : BaseContract.IBasePresenter {
        fun loadData()
    }
}