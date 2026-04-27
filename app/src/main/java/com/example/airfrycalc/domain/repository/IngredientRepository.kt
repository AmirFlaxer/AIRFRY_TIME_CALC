package com.example.airfrycalc.domain.repository

import com.example.airfrycalc.domain.model.Ingredient
import kotlinx.coroutines.flow.Flow

interface IngredientRepository {
    fun getAll(): Flow<List<Ingredient>>
    suspend fun add(ingredient: Ingredient): Long
    suspend fun delete(ingredient: Ingredient)
    suspend fun update(ingredient: Ingredient)
}
