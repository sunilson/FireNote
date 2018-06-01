package com.sunilson.firenote.presentation.homepage

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elementDialog.AddElementListener
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface HomepagePresenterContract {
    interface IHomepageView : IBaseView, AddElementListener {
        fun listElements(elements: List<Element>)
        fun loggedOut()
    }

    interface IHomepagePresenter {
        fun loadData()
    }
}