package com.sunilson.firenote.presentation.elements.bundle.di

import com.sunilson.firenote.presentation.elements.bundle.BundleFragment
import com.sunilson.firenote.presentation.elements.bundle.BundlePresenter
import com.sunilson.firenote.presentation.elements.bundle.BundlePresenterContract
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import dagger.Binds
import dagger.Module

@Module
abstract class BundleModule {

    @FragmentScope
    @Binds
    abstract fun bindBundleFragment(bundleFragment: BundleFragment) : BundlePresenterContract.View

    @FragmentScope
    @Binds
    abstract fun bindBundlePresenter(bundlePresenter: BundlePresenter) : BundlePresenterContract.Presenter

}