package com.sunilson.firenote.presentation.elements.note

import com.sunilson.firenote.presentation.elements.elementActivity.ElementContentPresenterContract

interface NotePresenterContract {
    interface INotePresenter {
        fun loadNoteData()
        fun storeNoteText(text: String)
    }

    interface INoteView : ElementContentPresenterContract.View {
        fun noteTextChanged(text: String)
        fun finishTextEdit()
    }
}