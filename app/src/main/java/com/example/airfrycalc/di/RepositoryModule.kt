package com.example.airfrycalc.di

import com.example.airfrycalc.data.repository.IngredientRepositoryImpl
import com.example.airfrycalc.domain.repository.IngredientRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIngredientRepository(
        impl: IngredientRepositoryImpl
    ): IngredientRepository
}
