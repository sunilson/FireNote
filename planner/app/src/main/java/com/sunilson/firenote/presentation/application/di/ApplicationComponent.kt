package com.sunilson.firenote.presentation.application.di

import android.app.Application
import com.sunilson.firenote.presentation.application.BaseApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, ApplicationModule::class, ApplicationSingletonModule::class, ActivityBuilder::class])
interface ApplicationComponent {
    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
    fun inject(application: BaseApplication)
}