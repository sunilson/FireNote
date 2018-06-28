package com.sunilson.firenote.presentation.bin

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.ChangeType
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class BinPresenter @Inject constructor(private val view: BinPresenterContract.View, private val repository: IRepository) : BasePresenter(view), BinPresenterContract.Presenter {

    override fun loadElementData() {
        disposable.dispose()
        disposable.add(repository.loadElements(FirebaseAuth.getInstance().currentUser!!.uid).subscribe({
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun restoreElement(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}