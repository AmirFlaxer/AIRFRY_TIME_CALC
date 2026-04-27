package com.example.airfrycalc.domain.usecase

import com.example.airfrycalc.domain.model.Ingredient
import com.example.airfrycalc.domain.repository.IngredientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIngredientsUseCase @Inject constructor(
    private val repository: IngredientRepository
) {
    operator fun invoke(): Flow<List<Ingredient>> = repository.getAll()
}
