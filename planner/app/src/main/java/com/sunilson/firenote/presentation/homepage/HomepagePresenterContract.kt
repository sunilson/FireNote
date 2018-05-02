package com.sunilson.firenote.presentation.homepage

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.presenters.BaseContract

interface  HomepagePresenterContract {
    interface View : BaseContract.IBaseView {
        fun listElements(elements: List<Element>)
    }

    interface Presenter : BaseContract.IBasePresenter {
        fun loadData()
    }
}