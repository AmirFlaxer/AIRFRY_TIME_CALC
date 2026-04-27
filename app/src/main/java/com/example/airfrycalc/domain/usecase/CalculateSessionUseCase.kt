package com.example.airfrycalc.domain.usecase

import com.example.airfrycalc.domain.model.CookingSession
import com.example.airfrycalc.domain.model.Ingredient
import com.example.airfrycalc.domain.model.ScheduledStep
import javax.inject.Inject

class CalculateSessionUseCase @Inject constructor() {
    operator fun invoke(selected: List<Ingredient>): CookingSession {
        require(selected.isNotEmpty()) { "No ingredients selected" }
        val maxTime = selected.maxOf { it.cookTimeMinutes }
        val steps = selected
            .map { ingredient ->
                ScheduledStep(
                    ingredient = ingredient,
                    addAtMinute = maxTime - ingredient.cookTimeMinutes,
                    isFirst = ingredient.cookTimeMinutes == maxTime
                )
            }
            .sortedBy { it.addAtMinute }
        return CookingSession(steps = steps, totalDurationMinutes = maxTime)
    }
}
