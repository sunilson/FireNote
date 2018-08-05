package com.sunilson.firenote.presentation.elements.checklist.di

import com.sunilson.firenote.presentation.elements.checklist.ChecklistFragment
import com.sunilson.firenote.presentation.elements.checklist.ChecklistPresenter
import com.sunilson.firenote.presentation.elements.checklist.ChecklistPresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import dagger.Binds
import dagger.Module

@Module
abstract class ChecklistModule {

    @Binds
    @FragmentScope
    abstract fun bindChecklistPresenter(checklistPresenter: ChecklistPresenter) : ChecklistPresenterContract.Presenter

    @Binds
    @FragmentScope
    abstract fun bindChecklistView(checklistFragment: ChecklistFragment): ChecklistPresenterContract.View

}