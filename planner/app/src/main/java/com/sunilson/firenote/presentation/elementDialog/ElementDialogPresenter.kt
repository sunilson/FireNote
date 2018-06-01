package com.sunilson.firenote.presentation.elementDialog

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
        if(validateElement(element)) {
            disposable.add(firebaseRepository.storeElement(element).subscribe({
                view.showSuccess(view.mContext?.getString(R.string.element_added))
            }, { view.showError(it.message) }))
        } else {
         view.showError("Validation failed")
        }
    }

    private fun validateElement(element: Element) : Boolean {
        //TODO
        return true
    }
}