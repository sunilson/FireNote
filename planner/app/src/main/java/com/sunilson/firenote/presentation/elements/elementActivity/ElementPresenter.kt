package com.sunilson.firenote.presentation.elements.elementActivity

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.Element
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class ElementPresenter @Inject constructor(private val eventRepository: IRepository, private val view: BaseElementPresenterContract.View)
    : BaseElementPresenterContract.Presenter, BasePresenter(view) {

    override fun loadElementData(elementID: String, parent: String?) {
        disposable.add(eventRepository.loadElement(FirebaseAuth.getInstance().currentUser!!.uid, elementID, parent).subscribe({
            if (it != null) view.elementChanged(it)
            else view.elementRemoved()
        }, { view.elementRemoved() }))
    }

    override fun updateElement(element: Element) {
        eventRepository.updateElement(FirebaseAuth.getInstance().currentUser!!.uid, element)
    }

    override fun lockElement(locked: Boolean) {
        disposable.add(eventRepository.lockElement(FirebaseAuth.getInstance().currentUser!!.uid, view.element!!.elementID, locked, view.element!!.parent).subscribe({}, {
            Log.e("ElementPresenter", it.message, it)
            view.showError(view.mContext?.getString(R.string.master_password_not_set))
        }))
    }

    override fun onStop() {
        disposable.dispose()
    }
}