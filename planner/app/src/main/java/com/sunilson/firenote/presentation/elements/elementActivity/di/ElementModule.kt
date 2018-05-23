package com.sunilson.firenote.presentation.elements.elementActivity.di

import com.sunilson.firenote.presentation.elements.elementActivity.ElementPresenter
import com.sunilson.firenote.presentation.elements.elementActivity.ElementActivity
import com.sunilson.firenote.presentation.elements.BaseElementPresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class ElementModule {
    @ActivityScope
    @Binds
    abstract fun provideElementPresenter(elementPresenter: ElementPresenter) : BaseElementPresenterContract.Presenter

    @ActivityScope
    @Binds
    abstract fun provideElementView(elementActivity: ElementActivity) : BaseElementPresenterContract.View
}