package com.sunilson.firenote.presentation.application.di

import com.sunilson.firenote.data.FirebaseAuthService
import com.sunilson.firenote.data.FirebaseRepository
import com.sunilson.firenote.data.IAuthentication
import com.sunilson.firenote.data.IRepository
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ApplicationSingletonModule {
    @Binds
    @Singleton
    abstract fun provideFirebaseRepository(queryBuilder: FirebaseRepository) : IRepository

    @Binds
    @Singleton
    abstract fun provideAuthService(firebaseAuthService: FirebaseAuthService) : IAuthentication
}