package com.sunilson.firenote.presentation.elements.note

import com.sunilson.firenote.R
import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.presentation.shared.base.BasePresenter
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import javax.inject.Inject

@FragmentScope
class NotePresenter @Inject constructor(private val eventRepository: IFirebaseRepository, val view: NotePresenterContract.INoteView)
    : BasePresenter(view), NotePresenterContract.INotePresenter {

    override fun onStop() {
        super.onStop()
        disposable.dispose()
        view.stopEditMode()
    }

    override fun onCreate() {
        loadElementData()
    }

    override fun storeNoteText(text: String) {
        if(view.element != null) {
            disposable.add(eventRepository.storeNoteText(view.element!!.elementID, text).subscribe({
                view.showSuccess(view.mContext?.getString(R.string.saved_note))
            },{
                view.showError(view.mContext?.getString(R.string.save_note_error))
            }))
        }
    }

    override fun loadElementData() {
        if(view.element != null) {
            disposable.add(eventRepository.loadNoteContent(view.element!!.elementID).subscribe {
                view.noteTextChanged(it)
            })
        }
    }
}
