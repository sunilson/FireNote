package com.sunilson.firenote.presentation.homepage

import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract
import com.sunilson.firenote.presentation.shared.base.IBaseView
import com.sunilson.firenote.presentation.shared.interfaces.HasElementList

interface HomepagePresenterContract {
    interface IHomepageView : IBaseView, HasElementList {
        fun clearAdapter()
        fun loggedOut()
    }

    interface IHomepagePresenter : ElementContentPresenterContract.Presenter{
        fun deleteElement(id: String)
        fun signOut()
    }
}