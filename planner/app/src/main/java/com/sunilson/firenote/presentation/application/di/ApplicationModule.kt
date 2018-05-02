package com.sunilson.firenote.presentation.application.di

import com.sunilson.firenote.data.FirebaseRepository
import com.sunilson.firenote.data.QueryBuilder
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class ApplicationModule {

    @Binds
    @Singleton
    abstract fun provideFirebaseRepository(queryBuilder: QueryBuilder) : FirebaseRepository

}