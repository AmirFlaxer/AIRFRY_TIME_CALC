package com.example.airfrycalc.data.local

import androidx.room.*
import com.example.airfrycalc.data.local.entity.IngredientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {

    @Query("SELECT * FROM ingredients ORDER BY name ASC")
    fun getAll(): Flow<List<IngredientEntity>>

    @Query("SELECT COUNT(*) FROM ingredients")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: IngredientEntity): Long

    @Delete
    suspend fun delete(item: IngredientEntity)

    @Update
    suspend fun update(item: IngredientEntity)
}
