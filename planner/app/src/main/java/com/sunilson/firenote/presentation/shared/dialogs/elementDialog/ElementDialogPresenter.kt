package com.sunilson.firenote.presentation.shared.dialogs.elementDialog

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope
import javax.inject.Inject

@DialogFragmentScope
class ElementDialogPresenter @Inject constructor(
        val repository: IRepository,
        val view: ElementDialogPresenterContract.View
) : ElementDialogPresenterContract.Presenter, BasePresenter(view) {

    override fun addElement(element: Element) {
        if (validateElement(element)) {
            disposable.add(repository.storeElement(FirebaseAuth.getInstance().currentUser!!.uid, element).subscribe({
                view.showSuccess(view.mContext?.getString(R.string.element_added))
                updateWidget()
            }, { view.showError(it.message) }))
        } else {
            view.showError("Validation failed")
        }
    }

    override fun updateElement(element: Element) {
        if (validateElement(element)) {
            disposable.add(repository.updateElement(FirebaseAuth.getInstance().currentUser!!.uid, element).subscribe({
                view.showSuccess(view.mContext?.getString(R.string.element_added))
                updateWidget()
            }, { view.showError(it.message) }))
        } else view.showError("Validation failed")
    }

    override fun deleteElement(element: Element) {
        disposable.add(repository.deleteElement(FirebaseAuth.getInstance().currentUser!!.uid, element.elementID, element.parent).subscribe({
            view.showSuccess(view.mContext?.getString(R.string.element_removed))
            updateWidget()
        }, {
            view.showError(it.message)
        }))
    }

    private fun validateElement(element: Element): Boolean {
        //TODO
        return true
    }
}