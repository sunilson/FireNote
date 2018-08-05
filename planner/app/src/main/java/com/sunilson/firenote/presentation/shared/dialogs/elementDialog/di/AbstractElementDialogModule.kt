package com.sunilson.firenote.presentation.shared.dialogs.elementDialog.di

import com.sunilson.firenote.presentation.shared.dialogs.elementDialog.ElementDialog
import com.sunilson.firenote.presentation.shared.dialogs.elementDialog.ElementDialogPresenter
import com.sunilson.firenote.presentation.shared.dialogs.elementDialog.ElementDialogPresenterContract
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