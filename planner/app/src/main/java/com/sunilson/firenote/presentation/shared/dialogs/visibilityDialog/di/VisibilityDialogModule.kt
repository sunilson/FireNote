package com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.di

import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.CategoryFragment
import com.sunilson.firenote.presentation.shared.dialogs.visibilityDialog.ColorFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class VisibilityDialogModule {

    @ContributesAndroidInjector
    @FragmentScope
    abstract fun contributeCategoryFragment() : CategoryFragment

    @ContributesAndroidInjector
    @FragmentScope
    abstract fun contributeColorFragment() : ColorFragment

}