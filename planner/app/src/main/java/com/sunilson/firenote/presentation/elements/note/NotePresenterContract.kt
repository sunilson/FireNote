package com.sunilson.firenote.presentation.elements.note

import com.sunilson.firenote.presentation.shared.base.element.BaseElementPresenterContract

interface NotePresenterContract {
    interface INotePresenter {
        fun loadNoteData()
    }

    interface INoteView : BaseElementPresenterContract.IBaseElementView {

    }
}