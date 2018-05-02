package com.sunilson.firenote.presentation.homepage.di

import com.sunilson.firenote.presentation.homepage.HomepagePresenter
import com.sunilson.firenote.presentation.homepage.HomepagePresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class HomepageModule {
    @ActivityScope
    @Binds
    abstract fun provideHomepageBresenter(homepagePresenter: HomepagePresenter) : HomepagePresenterContract.Presenter
}