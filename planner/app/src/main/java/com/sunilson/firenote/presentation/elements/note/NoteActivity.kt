package com.sunilson.firenote.presentation.elements.note

import com.sunilson.firenote.presentation.shared.base.element.BaseElementActivity
import com.sunilson.firenote.presentation.shared.base.BaseContract
import javax.inject.Inject

class ChecklistActivity : BaseElementActivity(), NotePresenterContract.INoteView {

    @Inject
    lateinit var presenter: NotePresenter

    override fun addObserver(presenter: BaseContract.IBasePresenter) = lifecycle.addObserver(presenter)
    override fun showTutorial() {
    }
}