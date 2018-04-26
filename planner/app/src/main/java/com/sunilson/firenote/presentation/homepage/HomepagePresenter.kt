package com.sunilson.firenote.presentation.homepage

import com.sunilson.firenote.presentation.shared.BaseContract
import com.sunilson.firenote.presentation.shared.BasePresenter

class HomepagePresenter : BasePresenter(), HomepagePresenterContract.HomepagePresenter {

    override fun loadData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setView(view: BaseContract.IBaseView) = view.addObserver(this)

    override fun onStop() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStart() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}