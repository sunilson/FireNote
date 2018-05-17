package com.sunilson.firenote.presentation.application.di

import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.settings.SettingsActivity
import com.sunilson.firenote.presentation.shared.base.element.activities.ElementActivity
import com.sunilson.firenote.presentation.shared.base.element.di.BaseElementModule
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [])
    @ActivityScope
    abstract fun contributeMainActivityInjector(): MainActivity

    @ContributesAndroidInjector(modules = [BaseElementModule::class])
    abstract fun contributeElementActivity(): ElementActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeBinActivity(): BinActivity

    @ContributesAndroidInjector(modules = [])
    abstract fun contributeSettingsActivity(): SettingsActivity
}