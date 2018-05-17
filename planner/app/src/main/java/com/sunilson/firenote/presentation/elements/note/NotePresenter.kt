package com.sunilson.firenote.presentation.elements.note

import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import javax.inject.Inject

@FragmentScope
class NotePresenter @Inject constructor(private val eventRepository: IFirebaseRepository, val view: NotePresenterContract.INoteView)
    : BasePresenter(), NotePresenterContract.INotePresenter {

    init {
        view.addObserver(this)
    }

    override fun onStop() {
        view.finishTextEdit()
        disposable.dispose()
    }

    override fun onStart() {
        loadNoteData()
    }

    override fun onDestroy() {
    }

    override fun onCreate() {
    }

    override fun storeNoteText(text: String) = eventRepository.storeNoteText(view.element.elementID, text)

    override fun loadNoteData() {
        disposable.add(eventRepository.loadNote(view.element.elementID).subscribe {
            view.noteTextChanged(it)
        })
    }
}
