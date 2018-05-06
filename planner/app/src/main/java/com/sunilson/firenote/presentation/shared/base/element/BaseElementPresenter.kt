package com.sunilson.firenote.presentation.shared.base.element

import com.sunilson.firenote.data.FirebaseRepository
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.base.IBaseView

interface BaseElementPresenterContract {
    interface IBaseElementPresenter {
        fun loadElementData()
        fun lockElement(locked: Boolean)
    }

    interface IBaseElementView : IBaseView {
        fun showError(error: String)
        fun showTutorial()
        fun elementChanged(element: Element)
        fun elementRemoved()
        fun getElement() : Element
    }
}

abstract class BaseElementPresenter<T>(private val eventRepository: FirebaseRepository,
                                    private val baseView: BaseElementPresenterContract.IBaseElementView)
    : BaseElementPresenterContract.IBaseElementPresenter, BasePresenter() {

    protected val element : Element = baseView.getElement()

    override fun loadElementData() {
        disposable.add(eventRepository.loadElement(element.elementID, element.parent).subscribe({
            if(it != null) baseView.elementChanged(it)
            else baseView.elementRemoved()
        }, {
            baseView.showError("Error loading element!")
        }))
    }

    override fun lockElement(locked: Boolean) {
        eventRepository.lockElement(element.elementID, locked, element.parent)
    }

    override fun onStart() {
        loadElementData()
    }

    override fun onStop() {
        disposable.dispose()
    }
}
