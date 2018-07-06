package com.sunilson.firenote.presentation.bin.di

import android.content.Context
import com.sunilson.firenote.presentation.bin.BinActivity
import com.sunilson.firenote.presentation.bin.BinPresenter
import com.sunilson.firenote.presentation.bin.BinPresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import dagger.Binds
import dagger.Module

@Module
abstract class BinModule {

    @ActivityScope
    @Binds
    abstract fun provideContext(binActivity: BinActivity): Context

    @ActivityScope
    @Binds
    abstract fun contributeBinPresenter(binPresenter: BinPresenter) : BinPresenterContract.Presenter


    @ActivityScope
    @Binds
    abstract fun contributeBinView(binView: BinActivity) : BinPresenterContract.View

}