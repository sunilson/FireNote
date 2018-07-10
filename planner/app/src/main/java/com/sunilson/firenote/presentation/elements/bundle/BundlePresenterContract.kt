package com.sunilson.firenote.presentation.elements.bundle

import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList

interface BundlePresenterContract {
    interface  View : HasElementList, ElementContentPresenterContract.View

    interface  Presenter: ElementContentPresenterContract.Presenter {
        fun deleteBundleElement(id: String)
    }
}