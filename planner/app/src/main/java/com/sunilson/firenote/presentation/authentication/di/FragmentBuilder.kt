package com.sunilson.firenote.presentation.authentication.di

import android.text.LoginFilter
import com.sunilson.firenote.presentation.authentication.fragments.LoginFragment
import com.sunilson.firenote.presentation.authentication.fragments.RegisterFragment
import com.sunilson.firenote.presentation.authentication.fragments.ResetPasswordFragment
import com.sunilson.firenote.presentation.authentication.fragments.StartFragment
import com.sunilson.firenote.presentation.shared.di.scopes.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilder {

    @ContributesAndroidInjector
    @FragmentScope
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    @FragmentScope
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    @FragmentScope
    abstract fun contributeStartFragment(): StartFragment

    @ContributesAndroidInjector
    @FragmentScope
    abstract fun contributeResetFragment(): ResetPasswordFragment

}