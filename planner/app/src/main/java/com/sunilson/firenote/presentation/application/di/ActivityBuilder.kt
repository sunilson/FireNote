package com.sunilson.firenote.presentation.application.di

import com.sunilson.firenote.presentation.authentication.AuthenticationActivity
import com.sunilson.firenote.presentation.authentication.di.AuthModule
import com.sunilson.firenote.presentation.authentication.di.FragmentBuilder
import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity
import com.sunilson.firenote.presentation.elements.elementActivity.di.ElementFragmentBuilder
import com.sunilson.firenote.presentation.elements.elementActivity.di.ElementModule
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.homepage.di.HomepageModule
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.settings.di.SettingsActivityModule
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope
import com.sunilson.firenote.presentation.visibilityDialog.VisibilityDialog
import com.sunilson.firenote.presentation.visibilityDialog.di.VisibilityDialogModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [AuthModule::class, FragmentBuilder::class])
    @ActivityScope
    abstract fun contributeAuthenticationActivity(): AuthenticationActivity

    @ContributesAndroidInjector(modules = [HomepageModule::class])
    @ActivityScope
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector(modules = [ElementModule::class, ElementFragmentBuilder::class])
    @ActivityScope
    abstract fun contributeElementActivity(): ElementActivity


    @ContributesAndroidInjector(modules = [VisibilityDialogModule::class])
    @DialogFragmentScope
    abstract fun ccontributeVisibilityDialogFragment(): VisibilityDialog

    @ContributesAndroidInjector(modules = [SettingsActivityModule::class])
    @ActivityScope
    abstract fun contributeSettingsActivity(): SettingsActivity

    /*
    @ContributesAndroidInjector(modules = [ElementDialogModule::class])
    @DialogFragmentScope
    abstract fun contributeAddElementFragment(): ElementDialog
    */

    /*

    @ContributesAndroidInjector(modules = [])
    @ActivityScope
    abstract fun contributeBinActivity(): BinActivity


    */
}