package com.sunilson.firenote.presentation.application.di

import com.sunilson.firenote.presentation.settings.adapters.ChangePasswordDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ApplicationFragmentBuilder {

    @ContributesAndroidInjector(modules=[])
    @DialogFragmentScope
    abstract fun provideChangePasswordDialog() : ChangePasswordDialog

}