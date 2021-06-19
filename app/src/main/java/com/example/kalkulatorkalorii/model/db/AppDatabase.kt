package com.example.kalkulatorkalorii.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.kalkulatorkalorii.model.db.daos.DishEntityDao
import com.example.kalkulatorkalorii.model.db.daos.HydrationEntityDao
import com.example.kalkulatorkalorii.model.db.entities.DishEntity
import com.example.kalkulatorkalorii.model.db.entities.HydrationEntity

@Database(entities = [DishEntity::class, HydrationEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dishDao(): DishEntityDao
    abstract fun hydrationDao(): HydrationEntityDao
}


