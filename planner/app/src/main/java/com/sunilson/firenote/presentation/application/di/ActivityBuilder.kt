package com.sunilson.firenote.presentation.application.di

import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.elements.bundle.BundleActivity
import com.sunilson.firenote.presentation.elements.checklist.ChecklistActivity
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.elements.note.NoteActivity
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [])
    @ActivityScope
    abstract fun contributeMainActivityInjector() : MainActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeNoteActivity() : NoteActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeBundleActivity() : BundleActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeChecklistActivity() : ChecklistActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeBinActivity() : BinActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeSettingsActivity() : SettingsActivity
}