package com.example.airfrycalc.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.airfrycalc.domain.model.Ingredient

@Entity(tableName = "ingredients")
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val cookTimeMinutes: Int,
    val isDefault: Boolean = false
)

fun IngredientEntity.toDomain() = Ingredient(id, name, cookTimeMinutes, isDefault)
fun Ingredient.toEntity() = IngredientEntity(id, name, cookTimeMinutes, isDefault)
