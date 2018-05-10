package com.sunilson.firenote.presentation.elements.checklist

import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.base.IBaseView
import com.sunilson.firenote.presentation.shared.base.element.ElementActivity
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

interface ChecklistPresenterContract {
    interface IChecklistPresenter {

    }

    interface IChecklistView : IBaseView {

    }
}

@ActivityScope
class ChecklistPresenter @Inject constructor(val View: ChecklistPresenterContract.IChecklistView) : BasePresenter(), ChecklistPresenterContract.IChecklistPresenter {
    override fun onStop() {
    }

    override fun onStart() {
    }

    override fun onDestroy() {
    }

    override fun onCreate() {
    }
}

class ChecklistActivity : ElementActivity(), ChecklistPresenterContract.IChecklistView {

    @Inject
    lateinit var presenter: ChecklistPresenter

    override fun addObserver(presenter: BasePresenter) = lifecycle.addObserver(presenter)
    override fun showTutorial() {
    }
}