package com.sunilson.firenote.presentation.elements.bundle

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.ChangeType
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import javax.inject.Inject

class BundlePresenter @Inject constructor(
        private val view: BundlePresenterContract.View,
        private val repository: IRepository)
    : BasePresenter(view), BundlePresenterContract.Presenter {

    override fun onStart() {
        super.onStart()
        loadElementData()
    }

    override fun onStop() {
        super.onStop()
        disposable.dispose()
    }

    override fun deleteBundleElement(id: String) {
        disposable.add(repository.deleteElement(FirebaseAuth.getInstance().currentUser!!.uid, id, view.element?.elementID).subscribe())
    }

    override fun loadElementData() {
        disposable.dispose()
        disposable.add(repository.loadElements(FirebaseAuth.getInstance().currentUser!!.uid, view.element?.elementID).subscribe({
            updateWidget()
            when (it?.first) {
                ChangeType.ADDED -> view.elementAdded(it.second)
                ChangeType.REMOVED -> view.elementRemoved(it.second)
                ChangeType.CHANGED -> view.elementChanged(it.second)
            }
        }, {
            view.showError(it.message)
        }))
    }

}