package com.sunilson.firenote.presentation.elements.elementActivity

import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.elements.elementActivity.interfaces.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class ElementPresenter @Inject constructor(private val eventRepository: IFirebaseRepository, private val view: BaseElementPresenterContract.View)
    : BaseElementPresenterContract.Presenter, BasePresenter() {

    override fun loadElementData() {
        disposable.add(eventRepository.loadElement(view.element.elementID, view.element.parent).subscribe({
            if (it != null) view.elementChanged(it)
            else view.elementRemoved()
        }, {
            view.showError("Error loading element!")
        }))
    }

    override fun lockElement(locked: Boolean) {
        eventRepository.lockElement(view.element.elementID, locked, view.element.parent)
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