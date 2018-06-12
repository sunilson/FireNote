package com.sunilson.firenote.presentation.elements.elementActivity

import com.sunilson.firenote.R
import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class ElementPresenter @Inject constructor(private val eventRepository: IFirebaseRepository, private val view: BaseElementPresenterContract.View)
    : BaseElementPresenterContract.Presenter, BasePresenter(view) {

    override fun loadElementData(elementID: String, parent: String?) {
        disposable.add(eventRepository.loadElement(elementID, parent).subscribe({
            if (it != null) view.elementChanged(it)
            else view.elementRemoved()
        }, { view.elementRemoved() }))
    }

    override fun updateElement(element: Element) {
        eventRepository.updateElement(element)
    }

    override fun lockElement(locked: Boolean) {
        disposable.add(eventRepository.lockElement(view.element!!.elementID, locked, view.element!!.parent).subscribe({}, {
            view.showError(view.mContext?.getString(R.string.set_master_password))
        }))
    }

    override fun onStop() {
        disposable.dispose()
    }
}