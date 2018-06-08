package com.sunilson.firenote.presentation.elements.note

import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import javax.inject.Inject

@FragmentScope
class NotePresenter @Inject constructor(private val eventRepository: IFirebaseRepository, val view: NotePresenterContract.INoteView)
    : BasePresenter(view), NotePresenterContract.INotePresenter {

    override fun onStop() {
        super.onStop()
        view.finishTextEdit()
        disposable.dispose()
    }

    override fun onCreate() {
        loadElementData()
    }

    override fun storeNoteText(text: String) {
        if(view.element != null) eventRepository.storeNoteText(view.element!!.elementID, text)
    }

    override fun loadElementData() {
        if(view.element != null) {
            disposable.add(eventRepository.loadNoteContent(view.element!!.elementID).subscribe {
                view.noteTextChanged(it)
            })
        }
    }
}
