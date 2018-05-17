package com.sunilson.firenote.presentation.shared.base.element

import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.base.element.interfaces.BaseElementPresenterContract
import javax.inject.Inject


class ElementPresenter @Inject constructor(private val eventRepository: IFirebaseRepository, private val view: BaseElementPresenterContract.View)
    : BaseElementPresenterContract.Presenter, BasePresenter() {

    override fun loadElementData() {
        disposable.add(eventRepository.loadElement(view.getElement().elementID, view.getElement().parent).subscribe({
            if (it != null) view.elementChanged(it)
            else view.elementRemoved()
        }, {
            view.showError("Error loading element!")
        }))
    }

    override fun lockElement(locked: Boolean) {
        eventRepository.lockElement(view.getElement().elementID, locked, view.getElement().parent)
    }

    override fun onStop() {
        disposable.dispose()
    }

    override fun onStart() {
        loadElementData()
    }

    override fun onDestroy() {
    }

    override fun onCreate() {
    }
}