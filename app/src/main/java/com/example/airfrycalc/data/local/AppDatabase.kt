package com.example.airfrycalc.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.airfrycalc.data.local.entity.IngredientEntity

@Database(entities = [IngredientEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ingredientDao(): IngredientDao
}
