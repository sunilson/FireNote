package com.sunilson.firenote.presentation.bin

import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.base.IBaseView
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class BinPresenter @Inject constructor(private val view: BinPresenterContract.View) : BasePresenter(view), BinPresenterContract.Presenter {

    override fun onStop() {}

    override fun onStart() {}

    override fun onDestroy() {}

    override fun onCreate() {}

    override fun loadData() {}
}