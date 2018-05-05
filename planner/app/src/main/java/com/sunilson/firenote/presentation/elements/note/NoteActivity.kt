package com.sunilson.firenote.presentation.elements.note

import com.sunilson.firenote.data.models.Note
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.base.element.BaseElementActivity

class ChecklistActivity : BaseElementActivity<Note>(), NotePresenterContract.INoteView {

    override fun addObserver(presenter: BasePresenter) = lifecycle.addObserver(presenter)
    override fun showTutorial() {
    }
}