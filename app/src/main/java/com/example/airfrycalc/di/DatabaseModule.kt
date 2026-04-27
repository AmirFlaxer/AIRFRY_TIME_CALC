package com.example.airfrycalc.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.airfrycalc.data.local.AppDatabase
import com.example.airfrycalc.data.local.IngredientDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "airfry.db")
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    db.execSQL("INSERT INTO ingredients (name, cookTimeMinutes, isDefault) VALUES ('נקניקיות רגילות', 9, 1)")
                    db.execSQL("INSERT INTO ingredients (name, cookTimeMinutes, isDefault) VALUES ('נקניקיות עבות', 12, 1)")
                    db.execSQL("INSERT INTO ingredients (name, cookTimeMinutes, isDefault) VALUES ('טבעות בצל רגילות', 5, 1)")
                    db.execSQL("INSERT INTO ingredients (name, cookTimeMinutes, isDefault) VALUES ('טבעות בצל שחומות', 7, 1)")
                }
            })
            .build()
    }

    @Provides
    fun provideIngredientDao(db: AppDatabase): IngredientDao = db.ingredientDao()
}
