package com.sunilson.firenote.presentation.homepage.di

import android.content.Context
import com.sunilson.firenote.presentation.shared.dialogs.elementDialog.ElementDialog
import com.sunilson.firenote.presentation.shared.dialogs.elementDialog.di.AbstractElementDialogModule
import com.sunilson.firenote.presentation.shared.dialogs.elementDialog.di.ElementDialogModule
import com.sunilson.firenote.presentation.homepage.HomepagePresenter
import com.sunilson.firenote.presentation.homepage.HomepagePresenterContract
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomepageModule {
    @ActivityScope
    @Binds
    abstract fun provideContext(mainActivity: MainActivity): Context

    @ActivityScope
    @Binds
    abstract fun provideHomepagePresenter(homepagePresenter: HomepagePresenter): HomepagePresenterContract.IHomepagePresenter

    @ActivityScope
    @Binds
    abstract fun provideMainActivity(mainActivity: MainActivity): HomepagePresenterContract.IHomepageView

    @DialogFragmentScope
    @ContributesAndroidInjector(modules = [AbstractElementDialogModule::class, ElementDialogModule::class])
    abstract fun contributeAddElementFragment(): ElementDialog
}