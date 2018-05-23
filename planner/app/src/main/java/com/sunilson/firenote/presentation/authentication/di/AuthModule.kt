package com.sunilson.firenote.presentation.authentication.di

import com.sunilson.firenote.presentation.authentication.AuthenticationActivity
import com.sunilson.firenote.presentation.authentication.AuthenticationPresenter
import com.sunilson.firenote.presentation.authentication.AuthenticationPresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class AuthModule {

    @ActivityScope
    @Binds
    abstract fun provideAuthPresenter(authenticationPresenter: AuthenticationPresenter): AuthenticationPresenterContract.Presenter

    @ActivityScope
    @Binds
    abstract fun provideAuthActivity(authenticationActivity: AuthenticationActivity): AuthenticationPresenterContract.View

}