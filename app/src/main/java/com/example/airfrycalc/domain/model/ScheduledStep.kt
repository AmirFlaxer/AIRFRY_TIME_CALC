package com.example.airfrycalc.domain.model

data class ScheduledStep(
    val ingredient: Ingredient,
    val addAtMinute: Int,
    val isFirst: Boolean
)
