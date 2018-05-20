package com.sunilson.firenote.presentation.application.di

import android.app.Application
import android.content.Context
import com.sunilson.firenote.data.IFirebaseRepository
import com.sunilson.firenote.data.FirebaseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule {
    @Provides
    @Singleton
    fun provideContext(application: Application) : Context {
        return application
    }
}