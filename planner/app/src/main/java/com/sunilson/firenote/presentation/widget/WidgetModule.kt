package com.sunilson.firenote.presentation.widget

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class WidgetModule {

    @ContributesAndroidInjector
    abstract fun contributeFirenoteWidget() : FirenoteWidgetProvider

    @ContributesAndroidInjector
    abstract fun contributeFirenoteWidgetRemoteService() : WidgetRemoteViewsService

}