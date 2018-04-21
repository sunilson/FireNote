package com.sunilson.firenote.presentation.homepage

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.BaseContract

interface HomepagePresenterContract {
    interface HomepageView : BaseContract.IBaseView {
        fun displayList(elements: ArrayList<Element>)
        fun elementAdded(element: Element)
        fun elementChanged(element: Element)
        fun elementRemoved(element: Element)
    }

    interface HomepagePresenter : BaseContract.IBasePresenter<HomepageView> {
        fun loadData()
    }
}