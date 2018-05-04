package com.sunilson.firenote.presentation.homepage

import com.google.firebase.auth.FirebaseAuth
import com.sunilson.firenote.data.FirebaseRepository
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.base.BaseContract
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import javax.inject.Inject

@ActivityScope
class HomepagePresenter @Inject constructor(val firebaseRepository: FirebaseRepository)
    : BasePresenter(), HomepagePresenterContract.Presenter {

    private lateinit var view: HomepagePresenterContract.View

    override fun loadData() {
        disposable.dispose()
        val user = FirebaseAuth.getInstance().currentUser
        if(user != null) {
            disposable.add(firebaseRepository.loadElements(user).subscribe { result, err ->
                view.listElements(result)
            })
        }
    }

    override fun setView(view: BaseContract.IBaseView) {
        this.view = view as HomepagePresenterContract.View
        view.addObserver(this)
    }

    override fun onStop() {}

    override fun onStart() {}

    override fun onDestroy() {}

    override fun onCreate() {}
}