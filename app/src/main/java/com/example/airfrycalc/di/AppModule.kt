package com.example.airfrycalc.di

import com.example.airfrycalc.presentation.session.CurrentSessionHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideCurrentSessionHolder(): CurrentSessionHolder = CurrentSessionHolder()
}
