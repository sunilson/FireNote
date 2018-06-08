package com.sunilson.firenote.presentation.elementDialog.di

import com.sunilson.firenote.presentation.elementDialog.ElementDialog
import com.sunilson.firenote.presentation.elementDialog.ElementDialogPresenter
import com.sunilson.firenote.presentation.elementDialog.ElementDialogPresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope
import dagger.Binds
import dagger.Module

@Module
abstract class AbstractElementDialogModule {

    @DialogFragmentScope
    @Binds
    abstract fun provideElementDialogPresenter(elementDialogPresenter: ElementDialogPresenter): ElementDialogPresenterContract.Presenter

    @DialogFragmentScope
    @Binds
    abstract fun provideElementDialogView(elementDialog: ElementDialog): ElementDialogPresenterContract.View
}