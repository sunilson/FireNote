package com.sunilson.firenote.presentation.homepage.di

import com.sunilson.firenote.presentation.elementDialog.AddElementDialog
import com.sunilson.firenote.presentation.homepage.HomepagePresenter
import com.sunilson.firenote.presentation.homepage.HomepagePresenterContract
import com.sunilson.firenote.presentation.homepage.MainActivity
import com.sunilson.firenote.presentation.shared.di.scopes.ActivityScope
import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class HomepageModule {

    @ActivityScope
    @Binds
    abstract fun provideHomepageBresenter(homepagePresenter: HomepagePresenter): HomepagePresenterContract.IHomepagePresenter

    @ActivityScope
    @Binds
    abstract fun provideMainActivity(mainActivity: MainActivity): HomepagePresenterContract.IHomepageView

    @ContributesAndroidInjector(modules = [])
    @FragmentScope
    abstract fun contributeAddElementFragment(): AddElementDialog

}