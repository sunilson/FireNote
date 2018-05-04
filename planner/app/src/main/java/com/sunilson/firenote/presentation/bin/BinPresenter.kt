package com.sunilson.firenote.presentation.bin

import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.base.BaseContract
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import javax.inject.Inject

@ActivityScope
class BinPresenter @Inject constructor() : BasePresenter(), BinPresenterContract.Presenter {

    private lateinit var view: BinPresenterContract.View

    override fun onStop() {}

    override fun onStart() {}

    override fun onDestroy() {}

    override fun onCreate() {}

    override fun loadData() {}

    override fun setView(view: BaseContract.IBaseView) {
        this.view = view as BinPresenterContract.View
        view.addObserver(this)
    }
}