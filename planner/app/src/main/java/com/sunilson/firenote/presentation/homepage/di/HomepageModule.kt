package com.sunilson.firenote.presentation.homepage.di

import com.sunilson.firenote.presentation.homepage.HomepagePresenter
import com.sunilson.firenote.presentation.homepage.HomepagePresenterContract
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class HomepageModule {
    @ActivityScope
    @Binds
    abstract fun provideHomepagePresenter(homepagePresenter: HomepagePresenter): HomepagePresenterContract.IHomepagePresenter

    @ActivityScope
    @Binds
    abstract fun provideMainActivity(mainActivity: MainActivity): HomepagePresenterContract.IHomepageView
}