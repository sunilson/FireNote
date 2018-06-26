package com.sunilson.firenote.presentation.settings.di

import com.sunilson.firenote.presentation.shared.di.scopes.DialogFragmentScope
import com.sunilson.firenote.presentation.shared.dialogs.ChangeLoginPasswordDialog
import com.sunilson.firenote.presentation.shared.dialogs.authenticationDialog.AuthenticationDialog
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class SettingsActivityModule {
    @ContributesAndroidInjector
    @DialogFragmentScope
    abstract fun contributeAuthenticationDialog(): AuthenticationDialog

    @ContributesAndroidInjector
    @DialogFragmentScope
    abstract fun contributeChangeLoginDialog(): ChangeLoginPasswordDialog
}