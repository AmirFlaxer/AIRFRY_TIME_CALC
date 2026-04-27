package com.example.airfrycalc.data.repository

import com.example.airfrycalc.data.local.IngredientDao
import com.example.airfrycalc.data.local.entity.toDomain
import com.example.airfrycalc.data.local.entity.toEntity
import com.example.airfrycalc.domain.model.Ingredient
import com.example.airfrycalc.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class IngredientRepositoryImpl @Inject constructor(
    private val dao: IngredientDao
) : IngredientRepository {

    override fun getAll(): Flow<List<Ingredient>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun add(ingredient: Ingredient): Long =
        dao.insert(ingredient.toEntity())

    override suspend fun delete(ingredient: Ingredient) =
        dao.delete(ingredient.toEntity())

    override suspend fun update(ingredient: Ingredient) =
        dao.update(ingredient.toEntity())
}
