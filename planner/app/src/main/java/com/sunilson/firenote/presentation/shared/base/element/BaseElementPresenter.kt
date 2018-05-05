package com.sunilson.firenote.presentation.shared.base.element

import com.sunilson.firenote.data.FirebaseRepository
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface BaseElementPresenterContract {
    interface IBaseElementPresenter {
        fun loadElementData(id: String, parent: String? = null)
        fun lockElement(id: String, locked: Boolean)
    }

    interface IBaseElementView : IBaseView {
        fun showError(error: String)
        fun showTutorial()
        fun elementChanged(element: Element)
        fun elementRemoved()
    }
}

abstract class BaseElementPresenter<T>(private val eventRepository: FirebaseRepository,
                                    private val baseView: BaseElementPresenterContract.IBaseElementView)
    : BaseElementPresenterContract.IBaseElementPresenter, BasePresenter() {
    override fun loadElementData(id: String, parent: String?) {
        eventRepository.loadElement(id, parent).subscribe({
            if(it != null) baseView.elementChanged(it)
            else baseView.elementRemoved()
        }, {
            baseView.showError("Error loading element!")
        })
    }
}
