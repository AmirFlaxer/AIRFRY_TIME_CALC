package com.example.airfrycalc.domain.model

data class Ingredient(
    val id: Long = 0,
    val name: String,
    val cookTimeMinutes: Int,
    val isDefault: Boolean = false
)
