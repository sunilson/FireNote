package com.sunilson.firenote.presentation.application.di

import com.sunilson.firenote.data.FirebaseRepository
import com.sunilson.firenote.data.IFirebaseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
abstract class ApplicationSingletonModule {
    @Binds
    @Singleton
    abstract fun provideFirebaseRepository(queryBuilder: FirebaseRepository) : IFirebaseRepository
}