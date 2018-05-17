package com.sunilson.firenote.presentation.homepage

import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.addElementDialog.AddElementListener
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface HomepagePresenterContract {
    interface IHomepageView : IBaseView, AddElementListener {
        fun listElements(elements: List<Element>)
    }

    interface IHomepagePresenter {
        fun loadData()
        fun addElement(element: Element)
    }
}