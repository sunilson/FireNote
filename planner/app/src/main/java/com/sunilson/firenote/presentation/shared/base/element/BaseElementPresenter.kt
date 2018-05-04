package com.sunilson.firenote.presentation.shared.base.element

import com.sunilson.firenote.presentation.shared.base.BaseContract

interface BaseElementPresenterContract {
    interface IBaseElementPresenter : BaseContract.IBasePresenter {
        fun loadElementData()
    }

    interface IBaseElementView : BaseContract.IBaseView {
        fun elementChanged()
        fun elementRemoved()
    }
}

abstract class BaseElementPresenter : BaseElementPresenterContract.IBaseElementPresenter {
    override fun loadElementData() {

    }
}
