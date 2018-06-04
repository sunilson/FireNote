package com.sunilson.firenote.presentation.homepage

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.R
import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class HomepagePresenter @Inject constructor(val firebaseRepository: IFirebaseRepository, val view: HomepagePresenterContract.IHomepageView)
    : BasePresenter(view), HomepagePresenterContract.IHomepagePresenter {

    private val authListener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
        if(it.currentUser == null) view.loggedOut()
    }

    override fun loadData() {
        disposable.dispose()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            disposable.add(firebaseRepository.loadElements(user).subscribe({ result ->
                view.listElements(result)
             }, {
                view.showError(it.message)
            }))
        }
    }

    override fun deleteElement(id: String) {
        disposable.add(firebaseRepository.deleteElement(id).subscribe {
            view.showSuccess(view.mContext?.getString(R.string.element_removed))
        })
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(authListener)
    }

    override fun onCreate() {
        loadData()
    }
}