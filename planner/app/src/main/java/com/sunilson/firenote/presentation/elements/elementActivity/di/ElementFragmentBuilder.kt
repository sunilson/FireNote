package com.sunilson.firenote.presentation.elements.elementActivity.di

import com.sunilson.firenote.presentation.elements.note.NoteFragment
import com.sunilson.firenote.presentation.elements.note.di.NoteModule
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ElementFragmentBuilder {

    @FragmentScope
    @ContributesAndroidInjector(modules = [NoteModule::class])
    abstract fun provideNoteFragment(): NoteFragment

}