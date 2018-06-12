package com.sunilson.firenote.presentation.dialogs.elementDialog

import com.sunilson.firenote.R
import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope
import javax.inject.Inject

@DialogFragmentScope
class ElementDialogPresenter @Inject constructor(
        val firebaseRepository: IFirebaseRepository,
        val view: ElementDialogPresenterContract.View
) : ElementDialogPresenterContract.Presenter, BasePresenter(view) {

    override fun addElement(element: Element) {
        if (validateElement(element)) {
            disposable.add(firebaseRepository.storeElement(element).subscribe({
                view.showSuccess(view.mContext?.getString(R.string.element_added))
            }, { view.showError(it.message) }))
        } else {
            view.showError("Validation failed")
        }
    }

    override fun updateElement(element: Element) {
        if (validateElement(element)) {
            disposable.add(firebaseRepository.updateElement(element).subscribe({
                view.showSuccess(view.mContext?.getString(R.string.element_added))
            }, { view.showError(it.message) }))
        } else view.showError("Validation failed")
    }

    override fun deleteElement(element: Element) {
        disposable.add(firebaseRepository.deleteElement(element.elementID, element.parent).subscribe({
            view.showSuccess(view.mContext?.getString(R.string.element_removed))
        }, {
            view.showError(it.message)
        }))
    }

    private fun validateElement(element: Element): Boolean {
        //TODO
        return true
    }
}