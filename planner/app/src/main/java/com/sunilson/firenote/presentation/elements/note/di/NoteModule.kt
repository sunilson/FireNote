package com.sunilson.firenote.presentation.elements.note.di

import com.sunilson.firenote.presentation.elements.note.NoteFragment
import com.sunilson.firenote.presentation.elements.note.NotePresenter
import com.sunilson.firenote.presentation.elements.note.NotePresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import dagger.Binds
import dagger.Module

@Module
abstract class NoteModule {
    @Binds
    @FragmentScope
    abstract fun bindNotePresenter(notePresenter: NotePresenter) : NotePresenterContract.INotePresenter

    @Binds
    @FragmentScope
    abstract fun bindNoteView(noteFragment: NoteFragment): NotePresenterContract.INoteView
}