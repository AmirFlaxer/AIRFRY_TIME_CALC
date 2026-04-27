package com.example.airfrycalc.domain.usecase

import com.example.airfrycalc.domain.model.Ingredient
import com.example.airfrycalc.domain.repository.IngredientRepository
import javax.inject.Inject

class AddIngredientUseCase @Inject constructor(
    private val repository: IngredientRepository
) {
    suspend operator fun invoke(ingredient: Ingredient): Long = repository.add(ingredient)
}
