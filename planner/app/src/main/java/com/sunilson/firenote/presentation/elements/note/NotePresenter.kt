package com.sunilson.firenote.presentation.elements.note

import com.sunilson.firenote.data.FirebaseRepository
import com.sunilson.firenote.data.models.Note
import com.sunilson.firenote.presentation.shared.base.element.BaseElementPresenter
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import javax.inject.Inject

@ActivityScope
class NotePresenter @Inject constructor(eventRepository: FirebaseRepository, val view: NotePresenterContract.INoteView)
    : BaseElementPresenter<Note>(eventRepository, view) {

    init {
        view.addObserver(this)
    }

    override fun onStop() {
    }

    override fun onStart() {
    }

    override fun onDestroy() {
    }

    override fun onCreate() {
    }
}
