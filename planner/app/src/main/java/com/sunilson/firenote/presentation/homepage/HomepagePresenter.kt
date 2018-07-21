package com.sunilson.firenote.presentation.homepage

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IAuthentication
import com.sunilson.firenote.data.IRepository
import com.sunilson.firenote.data.models.ChangeType
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class HomepagePresenter @Inject constructor(val repository: IRepository, val view: HomepagePresenterContract.IHomepageView, val authService: IAuthentication)
    : BasePresenter(view), HomepagePresenterContract.IHomepagePresenter {

    private val authListener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
        if (it.currentUser == null) view.loggedOut()
    }

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

    override fun deleteElement(id: String) {
        disposable.add(repository.deleteElement(FirebaseAuth.getInstance().currentUser!!.uid, id).subscribe {
            //view.showSuccess(view.mContext?.getString(R.string.element_removed))
        })
    }

    override fun restoreElement(id: String) {
        disposable.add(repository.restoreElement(FirebaseAuth.getInstance().currentUser!!.uid, id).subscribe( {
            view.showSuccess(view.mContext?.getString(R.string.element_restored))
        }, {
            view.showError(view.mContext?.getString(R.string.restore_error))
        }))
    }

    override fun signOut() {
        authService.signOut()
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
        if (FirebaseAuth.getInstance().currentUser != null) loadElementData()
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        disposable.dispose()
    }
}