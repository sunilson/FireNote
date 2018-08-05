package com.sunilson.firenote.presentation.bin

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.ChangeType
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class BinPresenter @Inject constructor(private val view: BinPresenterContract.View, private val repository: IRepository) : BasePresenter(view), BinPresenterContract.Presenter {

    override fun loadElementData() {
        disposable.dispose()
        disposable.add(repository.loadBinElements(FirebaseAuth.getInstance().currentUser!!.uid, view.parent).subscribe({
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

    override fun onStop() {
        disposable.dispose()
    }

    override fun onStart() {
        if (FirebaseAuth.getInstance().currentUser != null) loadElementData()
    }

    override fun clearElements() {
        disposable.add(repository.clearBin(FirebaseAuth.getInstance().currentUser!!.uid, view.parent).subscribe({
            view.showSuccess(view.mContext?.getString(R.string.cleared_elements))
        }, {
            view.showError(it.message)
        }))
    }

    override fun restoreElement(id: String) {
        disposable.add(repository.restoreElement(FirebaseAuth.getInstance().currentUser!!.uid,id,  view.parent).subscribe({
            view.showSuccess(view.mContext?.getString(R.string.element_restored))
        }, {
            view.showError(it.message)
        }))
    }

    override fun deleteElement(id: String) {
        disposable.add(repository.deleteBinElement(FirebaseAuth.getInstance().currentUser!!.uid, id, view.parent).subscribe({
            view.showSuccess(view.mContext?.getString(R.string.element_removed))
        }, {
            view.showError(it.message)
        }))
    }
}
