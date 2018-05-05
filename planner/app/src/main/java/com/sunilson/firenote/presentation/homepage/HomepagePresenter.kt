package com.sunilson.firenote.presentation.homepage

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.data.FirebaseRepository
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class HomepagePresenter @Inject constructor(val firebaseRepository: FirebaseRepository, val view: HomepagePresenterContract.IHomepageView)
    : BasePresenter(), HomepagePresenterContract.IHomepagePresenter {

    init {
        view.addObserver(this)
    }

    override fun loadData() {
        disposable.dispose()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            disposable.add(firebaseRepository.loadElements(user).subscribe { result, _ ->
                view.listElements(result)
            })
        }
    }

    override fun onStop() {}

    override fun onStart() {}

    override fun onDestroy() {}

    override fun onCreate() {}
}