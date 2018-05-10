package com.sunilson.firenote.presentation.shared.base.element.di

import com.sunilson.firenote.data.models.Bundle
import com.sunilson.firenote.data.models.Checklist
import com.sunilson.firenote.data.models.Note
import com.sunilson.firenote.presentation.elements.checklist.ChecklistPresenter
import com.sunilson.firenote.presentation.elements.note.NotePresenter
import com.sunilson.firenote.presentation.shared.base.element.ElementPresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class BaseElementModule {
    @Binds
    @ActivityScope
    abstract fun provideNotePresenter(notePresenter: NotePresenter) : ElementPresenterContract.IElementPresenter<Note>

    @Binds
    @ActivityScope
    abstract fun provideChecklistPresenter(checklistPresenter: ChecklistPresenter) : ElementPresenterContract.IElementPresenter<Checklist>

    @Binds
    @ActivityScope
    abstract fun provideBundlePresenter(bundlePresenter: BundlePresenter) : ElementPresenterContract.IElementPresenter<Bundle>
}