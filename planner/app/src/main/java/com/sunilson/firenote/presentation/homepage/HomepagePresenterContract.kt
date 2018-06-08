package com.sunilson.firenote.presentation.homepage

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface HomepagePresenterContract {
    interface IHomepageView : IBaseView {
        fun elementAdded(element: Element)
        fun elementRemoved(element: Element)
        fun elementChanged(element: Element)
        fun loggedOut()
    }

    interface IHomepagePresenter : ElementContentPresenterContract.Presenter{
        fun deleteElement(id: String)
    }
}