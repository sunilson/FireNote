package com.sunilson.firenote.presentation.elements.checklist

import com.sunilson.firenote.presentation.shared.base.element.BaseElementActivity
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.base.BaseContract
import javax.inject.Inject

interface ChecklistPresenterContract {
    interface IChecklistPresenter : BaseContract.IBasePresenter {

    }

    interface IChecklistView : BaseContract.IBaseView {

    }
}

@ActivityScope
class ChecklistPresenter @Inject constructor() : ChecklistPresenterContract.IChecklistPresenter {

    private lateinit var view: ChecklistPresenterContract.IChecklistView

    override fun setView(view: BaseContract.IBaseView) {
        this.view = view as ChecklistPresenterContract.IChecklistView
        this.view.addObserver(this)
    }
}

class ChecklistActivity : BaseElementActivity(), ChecklistPresenterContract.IChecklistView {

    @Inject
    lateinit var presenter: ChecklistPresenter

    override fun addObserver(presenter: BaseContract.IBasePresenter) = lifecycle.addObserver(presenter)
    override fun showTutorial() {
    }
}