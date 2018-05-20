package com.sunilson.firenote.presentation.application.di

import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.homepage.di.HomepageModule
import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity
import com.sunilson.firenote.presentation.elements.elementActivity.di.ElementModule
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [HomepageModule::class])
    @ActivityScope
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector(modules = [ElementModule::class])
    @ActivityScope
    abstract fun contributeElementActivity(): ElementActivity

    /*

    @ContributesAndroidInjector(modules = [])
    @ActivityScope
    abstract fun contributeBinActivity(): BinActivity

    @ContributesAndroidInjector(modules = [])
    @ActivityScope
    abstract fun contributeSettingsActivity(): SettingsActivity
    */
}